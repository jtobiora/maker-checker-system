package com.swiftfingers.makercheckersystem.service.auth;

import com.swiftfingers.makercheckersystem.repository.AuthRepository;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.utils.ReflectionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.swiftfingers.makercheckersystem.constants.AppConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalService {

    private final AuthRepository authorizationRepository;
    private final ReflectionUtils reflectionUtils;
    @Transactional
    public <T extends BaseEntity> T approveCreateAction(String entityName, Long id) {
        log.info("Approving create request for entity {} with id {}... ", entityName, id);
        return processApproval(entityName, id, AuthorizationStatus.INITIALIZED_CREATE, AuthorizationStatus.AUTHORIZED, CREATE);
    }

    @Transactional
    public <T extends BaseEntity> T approveUpdateAction(String entityName, Long id) {
        log.info("Approving update request for entity {} with id {}... ", entityName, id);
        return processApproval(entityName, id, AuthorizationStatus.INITIALIZED_UPDATE, AuthorizationStatus.AUTHORIZED, UPDATE);
    }

    @Transactional
    public <T extends BaseEntity> T approveToggleAction(String entityName, Long id) {
        log.info("Approving toggle request for entity {} with id {}... ", entityName, id);
        return processApproval(entityName, id, AuthorizationStatus.INITIALIZED_TOGGLE, AuthorizationStatus.AUTHORIZED, TOGGLE);
    }

    private <T extends BaseEntity> T processApproval(String entityName, Long id, AuthorizationStatus expectedStatus, AuthorizationStatus newStatus, String action) {
        try {
            T entity = authorizationRepository.findAndValidateEntity(entityName, id, expectedStatus);
            if (!action.equals(CREATE)) {
                reflectionUtils.pullEntityFromJson(entity);
            }
            authorizationRepository.updateEntityStatus(entity, newStatus, true, null);
            return authorizationRepository.save(entity);
        } catch (ClassNotFoundException e) {
            log.error("Error processing request ", e);
            throw new BadRequestException("Invalid entity name: " + entityName);
        }
    }
}
