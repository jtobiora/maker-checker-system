package com.swiftfingers.makercheckersystem.controller.upload_configurations.classes;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by Obiora on 30-Jul-2024 at 10:17
 */
@Data
@ToString
public class ApplicationSetting {
    private boolean registrationAllowed;
    private boolean emailVerificationRequired;
    private boolean kycVerificationRequired;
    private boolean automaticLogoutEnabled;
    private String timeZone;
    private String baseCurrency;
    private boolean donationAllowed;
    private boolean requestsAllowed;
    private BigDecimal minimumDonation;
    private BigDecimal maximumDonation;
    private Long maximumRequest;
    private Long minimumRequest;
}
