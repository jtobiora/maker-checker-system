package com.swiftfingers.makercheckersystem.controller.email_switch;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Obiora on 28-Jul-2024 at 11:28
 */
public interface EmailSettingsRepository extends JpaRepository<EmailSettings, Long> {
    EmailSettings findTopByOrderByIdDesc();
}
