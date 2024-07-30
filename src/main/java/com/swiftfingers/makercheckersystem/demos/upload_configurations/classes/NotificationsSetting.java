package com.swiftfingers.makercheckersystem.demos.upload_configurations.classes;

import lombok.Data;

/**
 * Created by Obiora on 30-Jul-2024 at 15:51
 */
@Data
public class NotificationsSetting {
    private String email;
    private boolean userRegistrationNotification;
    private boolean donationNotification;
    private boolean fundsRequestNotification;
    private boolean transactionNotification;
    private boolean usageReportNotification;
    private boolean systemMaintenanceNotification;
}
