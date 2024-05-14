package com.swiftfingers.makercheckersystem.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.swiftfingers.makercheckersystem.enums.TokenDestination;
import com.swiftfingers.makercheckersystem.payload.request.LoginRequest;
import com.swiftfingers.makercheckersystem.payload.request.PasswordResetRequest;
import com.swiftfingers.makercheckersystem.payload.request.TwoFactorAuthRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.payload.response.AuthenticationResponse;
import com.swiftfingers.makercheckersystem.service.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity <AuthenticationResponse> signIn (@Valid final @RequestBody LoginRequest loginRequest, HttpSession httpSession) throws JsonProcessingException {
        log.info("Authenticating user ... {}", loginRequest.getEmail());
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest, httpSession.getId()));
    }

    @PostMapping("/password-reset")
    public ResponseEntity<AppResponse> changePassword (final @NonNull @RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(authenticationService.changePassword(request));
    }

    @PostMapping("/2fa-setup")
    public ResponseEntity<AppResponse> setUp2fa (@Valid @RequestBody final TwoFactorAuthRequest authRequest) {
        return ResponseEntity.ok(authenticationService.setUp2Fa(authRequest));
    }

    @GetMapping("/2fa-token-confirmation/{token}")
    public ResponseEntity<AuthenticationResponse> confirm2FAToken (@PathVariable String token, @PathParam("email") String email, HttpSession session) {
        return ResponseEntity.ok(authenticationService.confirm2FaToken(token, email, session.getId()));
    }






}
