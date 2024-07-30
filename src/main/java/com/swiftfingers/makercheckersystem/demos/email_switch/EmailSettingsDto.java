package com.swiftfingers.makercheckersystem.demos.email_switch;

import lombok.Data;

/**
 * Created by Obiora on 28-Jul-2024 at 11:31
 */
@Data
public class EmailSettingsDto {
    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol; // e.g., smtp
    private boolean auth; // true or false
    private boolean starttls; // true or false
    private String sslTrust; // e.g., "smtp.mail.example.com"
    private String fromAddress;
}
