package com.swiftfingers.makercheckersystem.demos.upload_configurations.classes;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Obiora on 30-Jul-2024 at 10:23
 */
@Data
@ToString
public class ApplicationInfo {
    private String appName;
    private String appMainUrl;
    private String appTitle;
    private String appDescription;
    private String emailAddress;
    private String supportEmail;
    private String phoneNumber;
    private String contactAddress;
}
