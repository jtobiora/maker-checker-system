package com.swiftfingers.makercheckersystem.controller.email_switch;

import com.swiftfingers.makercheckersystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Obiora on 28-Jul-2024 at 11:35
 */
@RestController
@RequestMapping("/api/admin/email-settings")
@RequiredArgsConstructor
public class EmailSettingsController {
    private final EmailSettingsService emailSettingsService;

    private final MailConfig mailConfig;

    private final EmailService2 emailService;


    @PostMapping("/update")
    public void updateEmailSettings(@RequestBody EmailSettingsDto emailSettingsDto) {
        // Save settings to the database
        emailSettingsService.saveEmailSettings(emailSettingsDto);

        // Reload mail configuration to apply new settings
        mailConfig.reloadConfiguration();
    }

    @GetMapping("/current")
    public EmailSettingsDto getCurrentEmailSettings() {
        return emailSettingsService.getEmailSettings();
    }

    @PostMapping("/send-test-email")
    public String sendTestEmail(@RequestParam String to) {
        try {
            emailService.sendTokenEmail(to, "1234");
            return "Test email sent successfully.";
        } catch (Exception e) {
            return "Failed to send test email: " + e.getMessage();
        }
    }
}
