package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.payload.JwtSubject;
import com.swiftfingers.makercheckersystem.payload.request.LoginRequest;
import com.swiftfingers.makercheckersystem.payload.request.SignUpRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.payload.response.AuthenticationResponse;
import com.swiftfingers.makercheckersystem.repository.RoleAuthorityRepository;
import com.swiftfingers.makercheckersystem.repository.UserRoleRepository;
import com.swiftfingers.makercheckersystem.security.AuthPrincipal;
import com.swiftfingers.makercheckersystem.service.jwt.JwtTokenService;
import com.swiftfingers.makercheckersystem.service.redis.TokenCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthProvider authProvider;
    private final UserRoleRepository userRoleRepository;
    private final RoleAuthorityRepository roleAuthorityRepository;
    private final JwtTokenService tokenProvider;
    private final TokenCacheService tokenCacheService;

    public AppResponse registerUser(SignUpRequest request) {
        return null;
    }


    public AuthenticationResponse authenticate(LoginRequest loginRequest, String sessionId) {

       /*Creating UsernamePasswordAuthenticationToken object to send it to authentication manager.Attention! We used two parameters
        constructor. It sets authentication false by doing this.setAuthenticated(false);
        */
        UsernamePasswordAuthenticationToken authToken = new
                UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        //we let the custom authentication manager do its work
        Authentication auth = authProvider.authenticate(authToken);

        if (auth.isAuthenticated()) {
            String authoritiesString =  (String) auth.getDetails();
            SecurityContextHolder.getContext().setAuthentication(auth);

            UserDetails principal = (UserDetails) auth.getPrincipal();

            String userName = ((UserDetails) auth.getPrincipal()).getUsername();

            JwtSubject jwtSubject = new JwtSubject(userName, authoritiesString);

            //generate a token
            String token = tokenProvider.generateToken(jwtSubject, sessionId);

            //start a session in redis
            tokenCacheService.saveUserToken(sessionId, token);


            return AuthenticationResponse.builder().token(token).authorities(String.valueOf(principal.getAuthorities())).build();
        }

        throw new BadRequestException("User could not be authenticated");

    }


    public void recreateAuthentication(AuthPrincipal auth, String token, Set<GrantedAuthority> authorities) {
        auth.setAuthorities(authorities);
        auth.setToken(token);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
