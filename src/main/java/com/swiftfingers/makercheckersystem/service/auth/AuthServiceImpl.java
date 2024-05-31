package com.swiftfingers.makercheckersystem.service.auth;

import com.swiftfingers.makercheckersystem.repository.AuthRepository;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.enums.ModelState;
import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.payload.request.ApprovalRequest;
import com.swiftfingers.makercheckersystem.payload.request.RejectionRequest;
import com.swiftfingers.makercheckersystem.utils.EntityTypeResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.swiftfingers.makercheckersystem.constants.AppConstants.AUTHORIZATION_STATES_AVAILABLE;
import static com.swiftfingers.makercheckersystem.constants.AppConstants.FAILED_TO_LOAD_DATA;
import static com.swiftfingers.makercheckersystem.enums.AuthorizationStatus.AUTHORIZED;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl<T> implements AuthService<T> {
    private final AuthRepository authorizationRepository;
    private final ApprovalService approvalService;
    private final RejectionService rejectionService;

    @Override
    public Page<T> findEntityState(Class<T> entityClass, String authorizationType, Pageable pageable) {
        List<AuthorizationStatus> authStatusList = findAuthorizationStatus(authorizationType);
        if (ObjectUtils.isEmpty(authStatusList)) {
            throw new BadRequestException(AUTHORIZATION_STATES_AVAILABLE);
        }
        return authorizationRepository.findByAuthStatus(entityClass, authStatusList, pageable);
    }

    @Override
    @PreAuthorize("@permissionChecker.hasApprovePermission()")
    public BaseEntity approve(ApprovalRequest approvalRequest, Long entityId) {
        String className = EntityTypeResolver.getFullyQualifiedClassName(approvalRequest.getEntityName());
        log.info("Fully qualified class name: {}", className);
        return switch (approvalRequest.getActions()) {
            case APPROVE_CREATE -> approvalService.approveCreateAction(className, entityId);
            case APPROVE_UPDATE -> approvalService.approveUpdateAction(className, entityId);
            case APPROVE_TOGGLE -> approvalService.approveToggleAction(className, entityId);
            default -> throw new BadRequestException("Invalid approval action");
        };
    }

    @Override
    public BaseEntity reject(RejectionRequest rejectionRequest, Long entityId) {
        String className = EntityTypeResolver.getFullyQualifiedClassName(rejectionRequest.getEntityName());
        log.info("Fully qualified class name: {}", className);
        return switch (rejectionRequest.getActions()) {
            case REJECT_CREATE ->
                    rejectionService.rejectCreateAction(className, entityId, rejectionRequest.getReason());
            case REJECT_UPDATE ->
                    rejectionService.rejectUpdateAction(className, entityId, rejectionRequest.getReason());
            case REJECT_TOGGLE ->
                    rejectionService.rejectToggleAction(className, entityId, rejectionRequest.getReason());
            default -> throw new BadRequestException("Invalid rejection action");
        };
    }

    @Override
    public BaseEntity previewUpdate(Long id, String entityName, AuthorizationStatus expectedStatus) {
            String className = EntityTypeResolver.getFullyQualifiedClassName(entityName);
            return approvalService.getUpdatedEntity(className, id, expectedStatus);
    }

    private List<AuthorizationStatus> findAuthorizationStatus(String authorizationType) {
        if (authorizationType.equalsIgnoreCase(ModelState.UNAUTHORIZED.toString())) {
            return List.of(AuthorizationStatus.INITIALIZED_CREATE, AuthorizationStatus.INITIALIZED_UPDATE, AuthorizationStatus.INITIALIZED_TOGGLE);
        } else if (authorizationType.equalsIgnoreCase(ModelState.REJECTED.toString())) {
            return List.of(AuthorizationStatus.CREATION_REJECTED, AuthorizationStatus.UPDATE_REJECTED, AuthorizationStatus.TOGGLE_REJECTED);
        } else if (authorizationType.equalsIgnoreCase(ModelState.AUTHORIZED.toString())) {
            return List.of(AUTHORIZED);
        } else {
            return Collections.emptyList();
        }
    }
}
