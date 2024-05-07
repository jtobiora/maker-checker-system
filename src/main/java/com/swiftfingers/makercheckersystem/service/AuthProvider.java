package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import com.swiftfingers.makercheckersystem.security.AppAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthProvider implements AuthenticationProvider {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth.isAuthenticated()){
            log.debug("Already authenticated. return same");
            return auth;
        }

        String username = auth.getName();
        String password = String.valueOf(auth.getCredentials());

        userService.findByEmail(username);

       UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password, null);

       return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
       return UsernamePasswordAuthenticationToken.class.equals(authenticationType);
    }

    public boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
