package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.enums.Status;
import com.swiftfingers.makercheckersystem.model.PendingAction;
import com.swiftfingers.makercheckersystem.repository.PendingActionRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Created by Obiora on 29-May-2024 at 10:22
 */

@Service
@Slf4j
public class NotificationService {
    private final PendingActionRepository pendingActionRepository;
    private final EmailService emailService;
    private final Executor executor;

    public NotificationService(PendingActionRepository pendingActionRepository, EmailService emailService,
                               @Qualifier("taskExecutor") Executor executor) {
        this.pendingActionRepository = pendingActionRepository;
        this.emailService = emailService;
        this.executor = executor;
    }

    private void sendSingleActionEmail(PendingAction action, String authorizerEmail) throws MessagingException {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("actions", List.of(action)); // Put the single action in a list

        emailService.sendMailToAuthorizer(authorizerEmail, "Pending Action Notification", templateModel);
    }


    @Async("taskExecutor")
    public CompletableFuture<Void> sendApprovalNotification (String actionType, Long referenceId, String authorizerEmail, String initiatedBy, String referenceTable) {
        return CompletableFuture.runAsync(() -> {
           try {
               PendingAction pendingAction = PendingAction.builder()
                       .actionType(actionType)
                       .referenceId(referenceId)
                       .authorizerEmail("jtobiora@gmail.com")
                       .status(Status.PENDING)
                       .initiatedBy(initiatedBy)
                       .initiatedAt(LocalDateTime.now())
                       .referenceTable(referenceTable)
                       .build();
               log.info("Sending notification for {} ", pendingAction);
               pendingActionRepository.save(pendingAction);

               sendSingleActionEmail(pendingAction, authorizerEmail);
           } catch (Exception e) {
               log.error("Error sending notification ", e);
           }
        }, executor);

    }
}
