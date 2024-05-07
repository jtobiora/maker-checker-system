package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.payload.request.LoginRequest;
import com.swiftfingers.makercheckersystem.payload.request.SignUpRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.payload.response.AuthenticationResponse;
import com.swiftfingers.makercheckersystem.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

//    @PostMapping("/register")
//    public ResponseEntity <AppResponse> createUser (@Valid @RequestBody SignUpRequest signUpRequest) {
//        return ResponseEntity.ok(authenticationService.registerUser(signUpRequest));
//    }

    @PostMapping("/login")
    public ResponseEntity <AuthenticationResponse> authenticate (@Valid final @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
    }


}
