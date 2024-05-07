package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.SeederTracker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeederRepository extends JpaRepository <SeederTracker, Long> {
}
