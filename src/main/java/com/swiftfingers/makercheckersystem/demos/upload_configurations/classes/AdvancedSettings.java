package com.swiftfingers.makercheckersystem.demos.upload_configurations.classes;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Created by Obiora on 30-Jul-2024 at 10:26
 */
@Data
@ToString
public class AdvancedSettings {
    private String title;
    private String description;
    private String externalLink;
    private LocalDateTime expirationDate;
}
