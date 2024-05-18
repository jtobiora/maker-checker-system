package com.swiftfingers.makercheckersystem.service;


import com.swiftfingers.makercheckersystem.exceptions.AppException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import java.util.ArrayList;
import java.util.List;

import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.TOKEN_SUBJECT;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${mails.from}")
    private String from;

    @Async
    public void sendTokenEmail(String recipientEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject(TOKEN_SUBJECT);
            helper.setFrom(from);

            // Create Thymeleaf context and add token as a variable
            Context context = new Context();
            context.setVariable("token", token);

            // Process the email template with Thymeleaf
            String emailContent = templateEngine.process("token-email-template", context);
            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException ex) {
            log.error("Could not send mail ", ex);
        }
    }

    @Async
    public void sendPasswordEmail(String recipientEmail, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject("Your New Password");
            helper.setFrom(from);

            // Create Thymeleaf context and add dynamic variables
            Context context = new Context();
            context.setVariable("title", "Welcome to SwiftFingers");
            context.setVariable("password", password);
            context.setVariable("disclaimer", "If you did not request this password change, please ignore this email. Your account is secure.");

            // Process the email template with Thymeleaf
            String emailContent = templateEngine.process("password-email-template", context);
            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException ex) {
            log.error("Could not send mail ", ex);
        }
    }
    public boolean isValidEmail(String email) {
        // Check email syntax
        if (!isValidEmailSyntax(email)) {
            log.info("Invalid email syntax: {}", email);
            return false;
        }

        // Extract domain from email
        String domain = extractDomain(email);

        // Verify domain's MX records
        List<String> mxRecords = getMXRecords(domain);
        if (mxRecords.isEmpty()) {
            log.info("No MX records found for domain: {}", domain);
            return false;
        } else {
            log.info("MX records found for domain " + domain + ": " + mxRecords);
            return true;
        }
    }

    private boolean isValidEmailSyntax(String email) {
        // Use regex pattern to check email syntax
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private String extractDomain(String email) {
        // Extract domain from email address
        String[] parts = email.split("@");
        return parts[1];
    }

    private List<String> getMXRecords(String domain) {
        List<String> mxRecords = new ArrayList<>();
        try {
            Lookup lookup = new Lookup(domain, Type.MX);
            Record[] records = lookup.run();
            if (records != null) {
                for (Record record : records) {
                    if (record instanceof MXRecord) {
                        MXRecord mxRecord = (MXRecord) record;
                        String mx = mxRecord.getTarget().toString();
                        mxRecords.add(mx);
                    }
                }
            }
        } catch (Exception e) {
            // Handle DNS lookup error
            throw new AppException("Exception thrown while validating the email");
        }
        return mxRecords;
    }

}
