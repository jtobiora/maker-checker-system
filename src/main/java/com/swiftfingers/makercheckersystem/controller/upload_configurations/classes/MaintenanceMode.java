package com.swiftfingers.makercheckersystem.controller.upload_configurations.classes;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Obiora on 30-Jul-2024 at 10:28
 */
@Data
@ToString
public class MaintenanceMode {
    private boolean maintenanceMode;
    private String noticeDescription;
}
