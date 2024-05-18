package com.swiftfingers.makercheckersystem.service.auth;

import com.swiftfingers.makercheckersystem.enums.ApprovalActions;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.enums.ModelState;
import com.swiftfingers.makercheckersystem.enums.RejectionActions;
import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.payload.request.ApprovalRequest;
import com.swiftfingers.makercheckersystem.payload.request.RejectionRequest;
import com.swiftfingers.makercheckersystem.repository.AuthorizationRepository;
import com.swiftfingers.makercheckersystem.repository.RoleRepository;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
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
import static com.swiftfingers.makercheckersystem.enums.AuthorizationStatus.AUTHORIZED;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationServiceImpl<T> implements AuthorizationService<T> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorizationRepository authorizationRepository;

    @Override
    public Page<T> findEntityState(Class<T> entityClass, String authorizationType, Pageable pageable) {
        List<AuthorizationStatus> authStatusList = findAuthorizationStatus(authorizationType);
        if (ObjectUtils.isEmpty(authStatusList)) {
            throw new BadRequestException(String.format(AUTHORIZATION_STATES_AVAILABLE));
        }
        return authorizationRepository.findByAuthStatus(entityClass, authStatusList, pageable);
    }

    @Override
    @PreAuthorize("@permissionChecker.hasApprovePermission()")
    public BaseEntity approve(ApprovalRequest approvalRequest, Long entityId)  {
        String className = EntityTypeResolver.getFullyQualifiedClassName(approvalRequest.getEntityName());
        log.info("Fully qualified class name: {}", className);
        List<AuthorizationStatus> authStatusList = findAuthorizationStatus(approvalRequest.getAuthorizationType());
        if (ObjectUtils.isEmpty(authStatusList)) {
            throw new BadRequestException(String.format(AUTHORIZATION_STATES_AVAILABLE));
        }

        if (approvalRequest.getActions().equals(ApprovalActions.APPROVE_CREATE)) {
            return authorizationRepository.approveCreateAction(className, entityId);
        } else if (approvalRequest.getActions().equals(ApprovalActions.APPROVE_UPDATE)) {
            return authorizationRepository.approveUpdateAction(className, entityId);
        } else if (approvalRequest.getActions().equals(ApprovalActions.APPROVE_TOGGLE)) {
            return authorizationRepository.approveToggleAction(className, entityId);
        }

        return null;
    }

    @Override
    public BaseEntity reject(RejectionRequest rejectionRequest, Long entityId) {
        String klassName = EntityTypeResolver.getFullyQualifiedClassName(rejectionRequest.getEntityName());
        log.info("Fully qualified class name: {}", klassName);
        List<AuthorizationStatus> authStatusList = findAuthorizationStatus(rejectionRequest.getAuthorizationType());
        if (ObjectUtils.isEmpty(authStatusList)) {
            throw new BadRequestException(String.format(AUTHORIZATION_STATES_AVAILABLE));
        }
        if (rejectionRequest.getActions().equals(RejectionActions.REJECT_CREATE)) {
            return authorizationRepository.rejectCreateAction(klassName, entityId, rejectionRequest.getReason());
        } else if (rejectionRequest.getActions().equals(RejectionActions.REJECT_UPDATE)) {

        } else if (rejectionRequest.getActions().equals(RejectionActions.REJECT_TOGGLE)) {

        }
        return null;
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
