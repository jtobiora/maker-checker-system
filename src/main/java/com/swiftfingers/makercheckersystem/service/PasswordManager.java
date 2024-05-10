package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordManager {

    private final PasswordEncoder passwordEncoder;
    public void checkPassword (String rawPassword, User user) {
        boolean passwordMatched = passwordEncoder.matches(rawPassword, user.getPassword());

        if (!passwordMatched) {
            throw new ResourceNotFoundException("Incorrect username or password!");
        }
    }
}
