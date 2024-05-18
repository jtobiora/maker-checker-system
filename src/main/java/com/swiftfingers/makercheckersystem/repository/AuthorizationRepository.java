package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.audits.annotations.ExcludeFromUpdate;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.exceptions.AppException;
import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.utils.MapperUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.swiftfingers.makercheckersystem.constants.AppConstants.APPROVAL_ERR_MSG;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AuthorizationRepository {
    private final EntityManager entityManager;
    private static final String AUTHORIZATION_STATUS_FIELD = "authorizationStatus";
    private static final String JSON_DATA_FIELD = "jsonData";

    public <T> Page<T> findByAuthStatus (Class<T> entityClass, List<?> values, Pageable pageable) {
        String fieldName = AUTHORIZATION_STATUS_FIELD;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        // Create an IN predicate
        Predicate inPredicate = root.get(fieldName).in(values);

        // Add the IN predicate to the query
        query.select(root).where(inPredicate);

        // Execute the query and return a page
        List<T> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long totalCount = getTotalCount(entityClass, fieldName, values);

        return new PageImpl<>(resultList, pageable, totalCount);
    }

    private <T> long getTotalCount(Class<T> entityClass, String fieldName, List<?> values) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> root = countQuery.from(entityClass);

        // Create an IN predicate
        Predicate inPredicate = root.get(fieldName).in(values);

        // Add the IN predicate to the count query
        countQuery.select(cb.count(root)).where(inPredicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    /*
    *  Finds entity by Id
    * */
    @Transactional
    public <T extends BaseEntity> T findEntityById (Class<T> entityClass, Long id) {
        T entity = entityManager.find(entityClass, id);
        if (entity == null) {
            throw new ResourceNotFoundException("Entity of type " + entityClass.getSimpleName() + " with ID " + id + " not found");
        }

        return entity;
    }

    /*
     * Approves entity after CREATE action is done
     * */
//    @Transactional
//    public <T extends BaseEntity> T approveCreateAction(String entityName, Long id) {
//        log.info("Approving create requests for entity {}... ", entityName);
//        try {
//            Class<T> entityClass = getEntityClass(entityName);
//            T entity = findEntityById(entityClass, id);
//
//            //Authorization status must be INITIALIZED_CREATE for the entity to be approved after creation
//            validateAuthorizationStatus(entity, AuthorizationStatus.INITIALIZED_CREATE);
//
//            entity.setAuthorizationStatus(AuthorizationStatus.AUTHORIZED);
//            entity.setActive(true);
//            entity.setJsonData(null);
//
//            // Save the updated entity
//            return entityManager.merge(entity);
//        } catch (ClassNotFoundException e) {
//            log.error("Error processing request ", e);
//            throw new BadRequestException("Invalid entity name: " + entityName);
//        }
//    }
    @Transactional
    public <T extends BaseEntity> T approveCreateAction(String entityName, Long id) {
        log.info("Approving create requests for entity {}... ", entityName);
        try {
            T entity = findAndValidateEntity(entityName, id, AuthorizationStatus.INITIALIZED_CREATE);

            updateEntityStatus(entity, AuthorizationStatus.AUTHORIZED, true, null);

            // Save the updated entity
            return entityManager.merge(entity);
        } catch (ClassNotFoundException e) {
            log.error("Error processing request ", e);
            throw new BadRequestException("Invalid entity name: " + entityName);
        }
    }

    @Transactional
    public <T extends BaseEntity> T rejectCreateAction(String entityName, Long id, String reason) {
        log.info("Rejecting create requests for entity {}... ", entityName);
        try {
            T entity = findAndValidateEntity(entityName, id, AuthorizationStatus.INITIALIZED_CREATE);

            updateEntityStatus(entity, AuthorizationStatus.CREATION_REJECTED, false, reason);

            // Save the updated entity
            return entityManager.merge(entity);
        } catch (ClassNotFoundException e) {
            log.error("Error processing request ", e);
            throw new BadRequestException("Invalid entity name: " + entityName);
        }
    }

    private <T extends BaseEntity> T findAndValidateEntity(String entityName, Long id, AuthorizationStatus expectedStatus) throws ClassNotFoundException {
        Class<T> entityClass = getEntityClass(entityName);
        T entity = findEntityById(entityClass, id);
        validateAuthorizationStatus(entity, expectedStatus);
        return entity;
    }

    private <T extends BaseEntity> void updateEntityStatus(T entity, AuthorizationStatus newStatus, boolean isActive, String reason) {
        entity.setAuthorizationStatus(newStatus);
        entity.setActive(isActive);
        entity.setJsonData(null);
        if (reason != null) {
            entity.setReason(reason); // Assuming there's a `setReason` method in `BaseEntity`
        }
    }

    /*
    * Approves entity after UPDATE action is done
    * */
    @Transactional
    public <T extends BaseEntity> T approveUpdateAction(String entityName, Long id) {
        log.info("Approving update requests for entity {}... ", entityName);
        try {
            Class<T> entityClass = getEntityClass(entityName);
            T entity = findEntityById(entityClass, id);
            validateAuthorizationStatus(entity, AuthorizationStatus.INITIALIZED_UPDATE);

            if (entity != null) {
                pullEntityFromJson(entity);
                return entityManager.merge(entity);
            }
        } catch (ClassNotFoundException ex) {
            log.error("Class not found ", ex);
            throw new BadRequestException("Invalid entity name: " + entityName);
        }
        return null;
    }

    @Transactional
    public <T extends BaseEntity> T approveToggleAction(String entityName, Long id) {
        log.info("Approving toggle requests for entity {}... ", entityName);
        try {
            Class<T> entityClass = getEntityClass(entityName);
            T entity = findEntityById(entityClass, id);
            validateAuthorizationStatus(entity, AuthorizationStatus.INITIALIZED_TOGGLE);
            if (entity != null) {
                pullEntityFromJson(entity);
                return entityManager.merge(entity);
            }
        } catch (ClassNotFoundException e) {
            log.error("Class not found ", e);
            throw new BadRequestException("Invalid entity name: " + entityName);
        }

        return null;
    }

    /*
    *  Pulls out the entity from the JSON string stored in 'jsonData' field and then uses it to update the entity passed
    *  in the argument
    * */
    private <T extends BaseEntity> void pullEntityFromJson(T entity) {
        //The JSON string is stored in a field named 'jsonData'
        // Find the 'jsonData' field in the class hierarchy (from the entity to its superclasses)
        try {
            Field jsonField = findField(entity.getClass(), JSON_DATA_FIELD);
            jsonField.setAccessible(true);
            String jsonString = (String) jsonField.get(entity);
            if (jsonString != null) {
                //convert the values in 'jsonData' field to a Map. The field stores the updated resource
                Map<String, Object> updateValues = MapperUtils.fromJSON(jsonString, Map.class);
                updateEntity(entity, updateValues);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error while updating entity ", e);
            throw new AppException("Error while updating entity: " + e.getMessage());
        }
    }

    /*
    * Updates the entity using the values pulled out from the JSON string and which is stored in a Map
    * @entity - the resource being updated
    * @updateValues - the values to be updated with
    * */
    private <T extends BaseEntity> void updateEntity(T entity, Map<String, Object> updateValues) {
        entity.setJsonData(null);
        entity.setAuthorizationStatus(AuthorizationStatus.AUTHORIZED);
        updateValues.forEach((key, value) -> {
            try {
                Field field = findField(entity.getClass(), key);
                field.setAccessible(true);
                if (field.getAnnotation(ExcludeFromUpdate.class) == null && updateValues.containsKey(key) && value != null) {
                    updateField(entity, field, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("Error while updating entity ", e);
            }
        });
    }

    /*
    * Updates the entity passed in as argument.
    * @entity - The entity to update
    * @field - The field of the entity to update
    * @value - The value to be updated
    * */
    private <T extends BaseEntity, V> void updateField(T entity, Field field, V value) throws IllegalAccessException {
        if (field.getType().isEnum()) {
            //The field is an enum
            updateEnumField(entity, field, (String) value);
        } else {
            //The field is a regular field not Enum
            field.set(entity, value);
        }
        log.info("Field name: {} --- Updated value: {}", field.getName(), value);
    }

    /*
    * Updates an enum field
    * */
    private <T extends BaseEntity> void updateEnumField(T entity, Field field, String value) throws IllegalAccessException {
        Enum<?>[] enumConstants = (Enum<?>[]) field.getType().getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            if (enumConstant.name().equals(value)) {
                field.set(entity, enumConstant);
                break;
            }
        }
    }

    /*
    * Finds any field in a class or its superclasses
    * */
    private Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), fieldName);
            } else {
                throw e; // Field not found in the entire class hierarchy
            }
        }
    }

    /*
    * Checks Authorization status to avoid illegal updates or creation
    * */
    private <T extends BaseEntity> void validateAuthorizationStatus(T entity, AuthorizationStatus requiredStatus) {
        if (!entity.getAuthorizationStatus().equals(requiredStatus)) {
            throw new BadRequestException(APPROVAL_ERR_MSG);
        }
    }

    /*
     * Gets the entity class from a fully qualified class name
     * */
    private <T extends BaseEntity> Class<T> getEntityClass(String entityName) throws ClassNotFoundException {
        return (Class<T>) Class.forName(entityName);
    }



}
