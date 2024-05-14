package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.service.EmailService;
import com.swiftfingers.makercheckersystem.service.redis.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/test")
@RequiredArgsConstructor
@RestController
public class TestController {
    private final TestRepository testRepository;
    private final TokenService tokenService;
    private final EmailService emailService;


    @PostMapping
    public void testMail () throws Exception {
        String username = "jt.banego@gmail.com";
        String token = tokenService.generate2FAToken(UUID.randomUUID().toString());
        emailService.sendTokenEmail(username, token);
      // mailSender.sendEmail(body);


      // emailService.sendMail("jt.banego@gmail.com","test mail","testing mail");

    }

    @GetMapping("/send-sms")
    public void sendSms() {
        //smsService.sendMessage("+2348035382525", "This is a test message");
    }
}
