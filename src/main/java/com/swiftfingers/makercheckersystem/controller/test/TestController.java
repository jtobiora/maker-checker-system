package com.swiftfingers.makercheckersystem.controller.test;

import com.swiftfingers.makercheckersystem.service.EmailService;
import com.swiftfingers.makercheckersystem.service.redis.LoginTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/test")
@RequiredArgsConstructor
@RestController
public class TestController {
    private final TestRepository testRepository;
    private final LoginTokenService tokenService;
    private final EmailService emailService;
    private final TestService testService;

//    @PostMapping
//    public void testMail () throws Exception {
//        String username = "jt.banego@gmail.com";
//       // String token = tokenService.generate2FAToken(UUID.randomUUID().toString());
//       // emailService.sendTokenEmail(username, token);
//      // mailSender.sendEmail(body);
//
//
//      // emailService.sendMail("jt.banego@gmail.com","test mail","testing mail");
//
//    }



}
