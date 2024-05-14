package com.swiftfingers.makercheckersystem.service;


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
            String emailContent = templateEngine.process("email-template", context);
            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException ex) {
            log.error("Could not send mail ", ex);
        }
    }


//    @Async
//    public void sendMail(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//        message.setFrom("obiora.okwubanego@seerbit.com");
//        mailSender.send(message);
//
//        log.info("mail sent successfully");
//    }

}
