package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.enums.Status;
import com.swiftfingers.makercheckersystem.model.PendingAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Created by Obiora on 29-May-2024 at 10:23
 */
public interface PendingActionRepository extends JpaRepository <PendingAction, Long> {
    List<PendingAction> findAllByStatus(Status status);

    @Query("SELECT pa FROM PendingAction pa WHERE pa.referenceId = ?1 AND pa.status = ?2")
    Optional<PendingAction> findByReferenceIdAndStatus(Long referenceId, Status status);
}
