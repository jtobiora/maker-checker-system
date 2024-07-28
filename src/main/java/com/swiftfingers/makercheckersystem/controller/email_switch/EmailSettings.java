package com.swiftfingers.makercheckersystem.controller.email_switch;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Created by Obiora on 28-Jul-2024 at 11:28
 */
@Entity
@Data
public class EmailSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Assuming there is only one settings record

    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol;
    private boolean auth;
    private boolean starttls;
    private String sslTrust;
    private String fromAddress;
}
