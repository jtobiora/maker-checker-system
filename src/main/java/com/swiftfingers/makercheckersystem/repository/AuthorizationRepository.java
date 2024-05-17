package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.swiftfingers.makercheckersystem.constants.AppConstants.APPROVAL_ERR_MSG;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AuthorizationRepository {
    private final EntityManager entityManager;
    private static final String AUTHORIZATION_STATUS_FIELD = "authorizationStatus";

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
        try {
            Class<T> entityClass = (Class<T>) Class.forName(entityName);
            T entity = findEntityById(entityClass, id);

        } catch (ClassNotFoundException ex){
            throw new BadRequestException("Invalid entity name: " + entityName);
        }

        return null;
    }


}
