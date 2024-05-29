package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.enums.Status;
import com.swiftfingers.makercheckersystem.model.PendingAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Obiora on 29-May-2024 at 10:23
 */
public interface PendingActionRepository extends JpaRepository <PendingAction, Long> {
    List<PendingAction> findAllByStatus(Status status);
}
