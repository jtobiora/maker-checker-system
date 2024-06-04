package com.swiftfingers.makercheckersystem.service.auth;

import com.swiftfingers.makercheckersystem.repository.AuthRepository;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.service.PendingActionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.swiftfingers.makercheckersystem.enums.Status.APPROVED;
import static com.swiftfingers.makercheckersystem.enums.Status.REJECTED;

@Service
@RequiredArgsConstructor
@Slf4j
public class RejectionService {

    private final AuthRepository authorizationRepository;
    private final PendingActionService pendingActionService;

    @Transactional
    public <T extends BaseEntity> T rejectCreateAction(String entityName, Long id, String reason) {
        log.info("Rejecting create request for entity {} with id {}... ", entityName, id);
        return processRejection(entityName, id, AuthorizationStatus.INITIALIZED_CREATE, AuthorizationStatus.CREATION_REJECTED, reason);
    }

    @Transactional
    public <T extends BaseEntity> T rejectUpdateAction(String entityName, Long id, String reason) {
        log.info("Rejecting update request for entity {} with id {}... ", entityName, id);
        return processRejection(entityName, id, AuthorizationStatus.INITIALIZED_UPDATE, AuthorizationStatus.UPDATE_REJECTED, reason);
    }

    @Transactional
    public <T extends BaseEntity> T rejectToggleAction(String entityName, Long id, String reason) {
        log.info("Rejecting toggle request for entity {} with id {}... ", entityName, id);
        return processRejection(entityName, id, AuthorizationStatus.INITIALIZED_TOGGLE, AuthorizationStatus.TOGGLE_REJECTED, reason);
    }

    private <T extends BaseEntity> T processRejection(String entityName, Long id, AuthorizationStatus expectedStatus, AuthorizationStatus newStatus, String reason) {
        T entity = authorizationRepository.findAndValidateEntity(entityName, id, expectedStatus);
        authorizationRepository.updateEntityStatus(entity, newStatus, false, reason);
        T saved = authorizationRepository.save(entity);

        //update the PendingAction table for the item approved
        pendingActionService.resolvePendingAction(id, REJECTED);

        return saved;
    }
}
