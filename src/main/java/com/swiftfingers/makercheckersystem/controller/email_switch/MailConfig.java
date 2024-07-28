package com.swiftfingers.makercheckersystem.controller.email_switch;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Created by Obiora on 28-Jul-2024 at 11:34
 */
@Service
public class MailConfig {
    @Autowired
    private EmailSettingsService emailSettingsService;

    private JavaMailSender mailSender;

    @PostConstruct
    public void init() {
        try {
            reloadConfiguration();
        } catch (Exception e) {
            System.out.println("Error occured " + e);
        }
    }

    public void reloadConfiguration() {
        EmailSettingsDto settings = emailSettingsService.getEmailSettings();
        if (settings == null) {
            throw new IllegalStateException("EmailSettingsDto cannot be null");
        }
        mailSender = createMailSender(settings);
    }

    private JavaMailSender createMailSender(EmailSettingsDto settings) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(getOrThrow(settings.getHost(), "Host"));
        mailSender.setPort(getOrThrow(settings.getPort(), "Port"));
        mailSender.setUsername(getOrThrow(settings.getUsername(), "Username"));
        mailSender.setPassword(getOrThrow(settings.getPassword(), "Password"));

        Properties props = new Properties();
        props.put("mail.transport.protocol", getOrThrow(settings.getProtocol(), "Protocol"));
        props.put("mail.smtp.auth", settings.isAuth());
        props.put("mail.smtp.starttls.enable", settings.isStarttls());
        props.put("mail.smtp.ssl.trust", settings.getSslTrust());

        mailSender.setJavaMailProperties(props);

        return mailSender;
    }

    private <T> T getOrThrow(T value, String name) {
        if (value == null || (value instanceof String && ((String) value).isEmpty())) {
            throw new IllegalArgumentException(name + " cannot be null or empty");
        }
        return value;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }
}
