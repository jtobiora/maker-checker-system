package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.model.audits.AuditTrail;
import com.swiftfingers.makercheckersystem.repository.AuditTrailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditTrailService {


    private final AuditTrailRepository auditTrailRepository;

    public void saveAuditTrail(AuditTrail auditTrail) {
        auditTrailRepository.save(auditTrail);
    }
}
