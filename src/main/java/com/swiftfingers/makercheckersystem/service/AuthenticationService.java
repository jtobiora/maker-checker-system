package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.payload.request.LoginRequest;
import com.swiftfingers.makercheckersystem.payload.request.SignUpRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.repository.RoleRepository;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthProvider authProvider;

   public AppResponse registerUser (SignUpRequest request) {
       return null;
   }


   public AppResponse authenticate (LoginRequest loginRequest) {

       //Authenticate the user request - email and password
       Authentication authenticated = authProvider.authenticate(
               new UsernamePasswordAuthenticationToken(
                       loginRequest.getEmail(),
                       loginRequest.getPassword()
               )
       );

       System.out.println("Is Authenticated::: " +authenticated.isAuthenticated());
       return null;
   }
}
