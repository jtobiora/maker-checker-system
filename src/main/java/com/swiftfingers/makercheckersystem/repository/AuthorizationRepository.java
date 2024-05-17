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

    @Transactional
    public <T extends BaseEntity> T findEntityById(Class<T> entityClass, Long id) {
        T entity = entityManager.find(entityClass, id);
        if (entity == null) {
            throw new ResourceNotFoundException("Entity of type " + entityClass.getSimpleName() + " with ID " + id + " not found");
        }

        return entity;
    }

    @Transactional
    public <T extends BaseEntity> T approveCreateAction(String entityName, Long id) {
        try {
            Class<T> entityClass = (Class<T>) Class.forName(entityName);
            T entity = findEntityById(entityClass, id);

            //Authorization status must be INITIALIZED_CREATE for the entity to be approved after creation
            if (!entity.getAuthorizationStatus().equals(AuthorizationStatus.INITIALIZED_CREATE)) {
                throw new BadRequestException(APPROVAL_ERR_MSG);
            }

            entity.setAuthorizationStatus(AuthorizationStatus.AUTHORIZED);
            entity.setActive(true);
            entity.setJsonData(null);

            // Save the updated entity
            entityManager.merge(entity);
            entityManager.flush();
            return entity;
        } catch (ClassNotFoundException e) {
            throw new BadRequestException("Invalid entity name: " + entityName);
        }
    }

    @Transactional
    public <T extends BaseEntity> T approveUpdateAction(String entityName, Long id) {
        log.info("Approving update requests for entity {}... ", entityName);
        try {
            Class<T> entityClass = (Class<T>) Class.forName(entityName);
            T entity = findEntityById(entityClass, id);

            //Authorization status must be INITIALIZED_UPDATE for the entity to be approved after update
            if (!entity.getAuthorizationStatus().equals(AuthorizationStatus.INITIALIZED_UPDATE)) {
                throw new BadRequestException(APPROVAL_ERR_MSG);
            }

            if (entity != null) {
                // Assuming the JSON string is stored in a field named 'jsonData'
                // Find the 'jsonData' field in the class hierarchy
                //Field jsonField = findJsonDataField(entityClass, "");
                Field jsonField = findField(entityClass, JSON_DATA_FIELD);
                jsonField.setAccessible(true);
                String jsonString = (String) jsonField.get(entity);
                if (jsonString != null) {
                    Map<String, Object> updateValues = MapperUtils.fromJSON(jsonString, Map.class);
                    updateEntity(entity, updateValues);
                    return entityManager.merge(entity);
                }
            }

        } catch (ClassNotFoundException ex){
            log.error("Class not found ", ex);
            throw new BadRequestException("Invalid entity name: " + entityName);
        } catch (NoSuchFieldException e) {
            log.error("Field does not exist", e);
            throw new AppException("Invalid field ");
        } catch (IllegalAccessException e) {
            log.error("Cannot access field", e);
            throw new AppException("Cannot access field");
        }

        return null;
    }

    private <T> void updateEntity(BaseEntity entity, Map<String, Object> updateValues) {
        Class<? extends BaseEntity> clazz = entity.getClass();

        //manually set the JSON data field to null
        entity.setJsonData(null);

        updateValues.forEach((key, value) -> {
            try {
                // Check if the field exists in the entity class or its superclass
                Field field = findField(clazz, key);
                field.setAccessible(true);
                if (field.getAnnotation(ExcludeFromUpdate.class) == null) {
                    // Check if the field type is an enum
                    if (field.getType().isEnum()) {
                        // Get the enum constants
                        Enum<?>[] enumConstants = (Enum<?>[]) field.getType().getEnumConstants();
                        // Iterate over enum constants to find a match with the value
                        for (Enum<?> enumConstant : enumConstants) {
                            if (enumConstant.name().equals(value)) {
                                field.set(entity, enumConstant);
                                break;
                            }
                        }
                    } else {
                        // Set the value directly for non-enum fields
                        field.set(entity, value);
                    }
                    log.info("Field name: {} --- Updated value: {}", field.getName(), value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("Error while updating entity ", e);
            }
        });
    }

//    // Method to find the 'jsonData' field in the class hierarchy
//    private Field findJsonDataField(Class<?> entityClass) throws NoSuchFieldException {
//        Field jsonField = null;
//        Class<?> currentClass = entityClass;
//        while (jsonField == null && currentClass != null) {
//            try {
//                jsonField = currentClass.getDeclaredField("jsonData");
//            } catch (NoSuchFieldException ignored) {
//                // Field not found in the current class, move up the hierarchy
//                currentClass = currentClass.getSuperclass();
//            }
//        }
//        if (jsonField == null) {
//            // 'jsonData' field not found in the class hierarchy
//            throw new NoSuchFieldException("jsonData");
//        }
//        return jsonField;
//    }

    // Method to find any field in a class or its superclass
    private Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // Field not found in the current class, try superclass
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), fieldName);
            } else {
                throw e; // Field not found in the entire class hierarchy
            }
        }
    }


}
