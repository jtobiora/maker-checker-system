package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordManager passwordManager;


    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth.isAuthenticated()) {
            log.debug("Already authenticated. return same");
        }

        String username = auth.getName();
        String password = String.valueOf(auth.getCredentials());
        User userFound = userRepository.findByUsernameOrEmail(username, username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        passwordManager.checkPassword(password, userFound);
        return new UsernamePasswordAuthenticationToken(userFound, password);
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return UsernamePasswordAuthenticationToken.class.equals(authenticationType);
    }

}
