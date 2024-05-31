package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.enums.Status;
import com.swiftfingers.makercheckersystem.model.PendingAction;
import com.swiftfingers.makercheckersystem.repository.PendingActionRepository;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

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
        // Format createdAt field in each PendingAction to the desired format
        List<PendingAction> pendingActions = List.of(action);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        pendingActions.forEach(a -> a.setCreatedAtFormatted(a.getCreatedAt().format(formatter)));
        templateModel.put("actions", pendingActions); // Put the single action in a list

        emailService.sendMailToAuthorizer(authorizerEmail, "Pending Action Notification", templateModel);
    }

    public CompletableFuture<Void> sendApprovalNotification (String actionType, Long referenceId, String authorizerEmail, String initiatedBy, String referenceTable) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return CompletableFuture.runAsync(() -> {
            try {
                // Set the security context in the new thread
                SecurityContextHolder.setContext(securityContext);

                PendingAction pendingAction = PendingAction.builder()
                        .actionType(actionType)
                        .referenceId(referenceId)
                        .authorizerEmail(authorizerEmail)
                        .status(Status.PENDING)
                        .referenceTable(referenceTable)
                        .build();

                log.info("Sending notification for {} ", pendingAction);
                pendingActionRepository.save(pendingAction);

                sendSingleActionEmail(pendingAction, authorizerEmail);
            } catch (Exception e) {
                log.error("Error sending notification ", e);
            } finally {
                  // Clear the security context after the task completes
                SecurityContextHolder.clearContext();
            }
        }, executor);

    }

    public void notifyAuthorizers() {
        log.info("Running a job to notify authorizers of pending actions ");
        List<PendingAction> pendingActions = pendingActionRepository.findAllByStatus(Status.PENDING);

        // Format createdAt field in each PendingAction to the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        pendingActions.forEach(action -> action.setCreatedAtFormatted(action.getCreatedAt().format(formatter)));

        // Group pending actions by authorizer email
        Map<String, List<PendingAction>> actionsGroupedByEmail = pendingActions.stream()
                .collect(Collectors.groupingBy(PendingAction::getAuthorizerEmail));

        // Process actions in batches for each authorizer
        actionsGroupedByEmail.forEach((authorizerEmail, actions) -> {
            for (int i = 0; i < actions.size(); i += 10) { // Process in batches of 10
                List<PendingAction> batch = actions.subList(i, Math.min(i + 10, actions.size()));
                try {
                    processBatch(batch, authorizerEmail);
                } catch (MessagingException e) {
                    log.error("Exception notifying authorizers ", e);
                }
            }
        });
    }

    private void processBatch(List<PendingAction> batch, String authorizerEmail) throws MessagingException {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("actions", batch);

        emailService.sendMailToAuthorizer(authorizerEmail, "Pending Actions Notification", templateModel);
    }
}
