package com.swiftfingers.makercheckersystem.audits.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftfingers.makercheckersystem.audits.annotations.CreateOperation;
import com.swiftfingers.makercheckersystem.audits.annotations.DeleteOperation;
import com.swiftfingers.makercheckersystem.audits.annotations.UpdateOperation;
import com.swiftfingers.makercheckersystem.model.audits.AuditTrail;
import com.swiftfingers.makercheckersystem.service.AuditTrailService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.swiftfingers.makercheckersystem.constants.AppConstants.*;
import static com.swiftfingers.makercheckersystem.utils.MapperUtils.serializeObjectExcludingSensitiveFields;


@Aspect
@Component
@RequiredArgsConstructor
public class AuditTrailAspect {

    private final AuditTrailService auditTrailService;

    private final ObjectMapper objectMapper;

    @AfterReturning("@annotation(createOperation)")
    public void logCreateOperation(JoinPoint joinPoint, CreateOperation createOperation) {
        logOperation(joinPoint, CREATE);
    }

    @AfterReturning("@annotation(updateOperation)")
    public void logUpdateOperation(JoinPoint joinPoint, UpdateOperation updateOperation) {
        logOperation(joinPoint, UPDATE);
    }

    @AfterReturning("@annotation(deleteOperation)")
    public void logDeleteOperation(JoinPoint joinPoint, DeleteOperation deleteOperation) {
        logOperation(joinPoint, DELETE);
    }

    private void logOperation(JoinPoint joinPoint, String action) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String entityName = args.length > 0 ? args[0].getClass().getSimpleName() : "";
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // Serialize data object to JSON excluding sensitive fields like passwords
        String jsonData = "";
        if (args.length > 0) {
            jsonData = serializeObjectExcludingSensitiveFields(args[0]);
        }

        AuditTrail auditTrail = AuditTrail.builder()
                .action(action)
                .className(className)
                .entity(entityName)
                .methodName(methodName)
                .build();

        auditTrail.setJsonData(jsonData);
        auditTrail.setActive(true);

        auditTrailService.saveAuditTrail(auditTrail);
    }



}
