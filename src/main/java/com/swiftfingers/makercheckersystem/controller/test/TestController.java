package com.swiftfingers.makercheckersystem.controller.test;

import com.swiftfingers.makercheckersystem.payload.response.EmailValidatorResponse;
import com.swiftfingers.makercheckersystem.service.EmailSender;
import com.swiftfingers.makercheckersystem.service.redis.LoginTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/test")
@RequiredArgsConstructor
@RestController
public class TestController {
    private final TestRepository testRepository;
    private final LoginTokenService tokenService;
    private final EmailSender emailService;
    private final TestService testService;

//    @GetMapping
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

    @GetMapping("/validate-email")
    public Object validateEmail(@RequestParam String email) {
        EmailValidatorResponse res = testService.validateEmail(email);

        if (res.isValid()) {
            return res.getMessage();
        } else {
           return res;
        }
    }


}
