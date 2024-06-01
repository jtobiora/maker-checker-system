package com.swiftfingers.makercheckersystem.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftfingers.makercheckersystem.enums.Status;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.repository.AuthRepository;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.service.PendingActionService;
import com.swiftfingers.makercheckersystem.utils.ReflectionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.swiftfingers.makercheckersystem.constants.AppConstants.*;
import static com.swiftfingers.makercheckersystem.enums.Status.APPROVED;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalService {

    private final AuthRepository authorizationRepository;
    private final ReflectionUtils reflectionUtils;
    private final PendingActionService pendingActionService;
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
            T entity = authorizationRepository.findAndValidateEntity(entityName, id, expectedStatus);
            if (!action.equals(CREATE)) {
                Map<String, Object> updateValuesMap = reflectionUtils.pullEntityFromJson(entity);
                ReflectionUtils.updateEntity(entity, updateValuesMap);
            }
            authorizationRepository.updateEntityStatus(entity, newStatus, true, null);
            T saved = authorizationRepository.save(entity);

            //update the PendingAction table for the item approved
            pendingActionService.resolvePendingAction(id, APPROVED);
            return saved;
    }

    public <T extends BaseEntity> T getUpdatedEntity (String entityName, Long id, AuthorizationStatus expectedStatus) {
        T entity = authorizationRepository.findAndValidateEntity(entityName, id, expectedStatus);
        Map<String, Object> updateInJsonMap = reflectionUtils.pullEntityFromJson(entity);

        if (!ObjectUtils.isEmpty(updateInJsonMap)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // Convert the map to an object of type T
                return objectMapper.convertValue(updateInJsonMap, (Class<T>) entity.getClass());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Error mapping out updated entity: ", e);
            }
        }

       throw new ResourceNotFoundException(FAILED_TO_LOAD_DATA);
    }
}
