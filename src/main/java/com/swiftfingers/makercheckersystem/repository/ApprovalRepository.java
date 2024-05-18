package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.exceptions.AppException;
import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.utils.MapperUtils;
import com.swiftfingers.makercheckersystem.utils.ReflectionUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ApprovalRepository {

    private final EntityManager entityManager;

    public <T extends BaseEntity> Page<T> findByAuthStatus(Class<T> entityClass, List<?> values, Pageable pageable) {
        String fieldName = "authorizationStatus";
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        Predicate inPredicate = root.get(fieldName).in(values);
        query.select(root).where(inPredicate);

        List<T> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long totalCount = getTotalCount(entityClass, fieldName, values);

        return new PageImpl<>(resultList, pageable, totalCount);
    }

    private <T extends BaseEntity> long getTotalCount(Class<T> entityClass, String fieldName, List<?> values) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> root = countQuery.from(entityClass);

        Predicate inPredicate = root.get(fieldName).in(values);
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
    public <T extends BaseEntity> T save(T entity) {
        return entityManager.merge(entity);
    }

    public <T extends BaseEntity> T findAndValidateEntity(String entityName, Long id, AuthorizationStatus expectedStatus) throws ClassNotFoundException {
        Class<T> entityClass = getEntityClass(entityName);
        T entity = findEntityById(entityClass, id);
        validateAuthorizationStatus(entity, expectedStatus);
        return entity;
    }

    public <T extends BaseEntity> void updateEntityStatus(T entity, AuthorizationStatus newStatus, boolean isActive, String reason) {
        entity.setAuthorizationStatus(newStatus);
        entity.setActive(isActive);
        entity.setJsonData(null);
        if (reason != null) {
            entity.setReason(reason); // Assuming there's a `setReason` method in `BaseEntity`
        }
    }

    public <T extends BaseEntity> void pullEntityFromJson(T entity) {
        try {
            Field jsonField = ReflectionUtils.findField(entity.getClass(), "jsonData");
            jsonField.setAccessible(true);
            String jsonString = (String) jsonField.get(entity);
            if (jsonString != null) {
                Map<String, Object> updateValues = MapperUtils.fromJSON(jsonString, Map.class);
                ReflectionUtils.updateEntity(entity, updateValues);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error while updating entity ", e);
            throw new AppException("Error while updating entity: " + e.getMessage());
        }
    }

    private <T extends BaseEntity> void validateAuthorizationStatus(T entity, AuthorizationStatus requiredStatus) {
        if (!entity.getAuthorizationStatus().equals(requiredStatus)) {
            throw new BadRequestException("Invalid authorization status");
        }
    }

    private <T extends BaseEntity> Class<T> getEntityClass(String entityName) throws ClassNotFoundException {
        return (Class<T>) Class.forName(entityName);
    }

}
