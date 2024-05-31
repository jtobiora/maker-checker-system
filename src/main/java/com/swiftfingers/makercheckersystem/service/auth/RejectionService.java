package com.swiftfingers.makercheckersystem.service.auth;

import com.swiftfingers.makercheckersystem.repository.AuthRepository;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RejectionService {

    private final AuthRepository authorizationRepository;

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
            return authorizationRepository.save(entity);
    }
}
