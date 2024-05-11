package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.audits.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditTrailRepository extends JpaRepository <AuditTrail, Long> {
}
