package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.payload.request.LoginRequest;
import com.swiftfingers.makercheckersystem.payload.response.AuthenticationResponse;
import com.swiftfingers.makercheckersystem.service.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

//    @PostMapping("/register")
//    public ResponseEntity <AppResponse> createUser (@Valid @RequestBody SignUpRequest signUpRequest) {
//        return ResponseEntity.ok(authenticationService.registerUser(signUpRequest));
//    }

    @PostMapping("/login")
    public ResponseEntity <AuthenticationResponse> signIn (@Valid final @RequestBody LoginRequest loginRequest, HttpSession httpSession) {
        log.info("Authenticating user ... {}", loginRequest.getEmail());
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest, httpSession.getId()));
    }

//    @PostMapping("/logout")
//    public ResponseEntity <AuthenticationResponse> logout (@RequestHeader(TOKEN_HEADER) String token) {
//        log.info("Logging out user... ");
//        return ResponseEntity.ok(authenticationService.authenticate(loginRequest, httpSession.getId()));
//    }


}
