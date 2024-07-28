package com.swiftfingers.makercheckersystem.controller.email_switch;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * Created by Obiora on 28-Jul-2024 at 11:40
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService2 {
    private final MailConfig mailConfig;

    private final SpringTemplateEngine templateEngine;
    private final EmailSettingsService emailSettingsService;

//    @Value("${mails.from}")
//    private String from;

    @Async
    public void sendTokenEmail (String recipientEmail, String token) {
        try {

            String from = emailSettingsService.getEmailSettings().getFromAddress(); // Retrieve 'from' address from the settings
            JavaMailSender mailSender = mailConfig.getMailSender();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject("Your Token");
            helper.setFrom(from);

            Context context = new Context();
            context.setVariable("token", token);
            String emailContent = templateEngine.process("token-email-template", context);
            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException ex) {
            log.error("Could not send mail ", ex);
        }
    }
}
