package com.swiftfingers.makercheckersystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.swiftfingers.makercheckersystem.constants.SecurityMessages;
import com.swiftfingers.makercheckersystem.enums.TokenDestination;
import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.user.PasswordHistory;
import com.swiftfingers.makercheckersystem.model.user.Token;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.payload.JwtSubject;
import com.swiftfingers.makercheckersystem.payload.request.LoginRequest;
import com.swiftfingers.makercheckersystem.payload.request.PasswordResetRequest;
import com.swiftfingers.makercheckersystem.payload.request.SignUpRequest;
import com.swiftfingers.makercheckersystem.payload.request.TwoFactorAuthRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.payload.response.AuthenticationResponse;
import com.swiftfingers.makercheckersystem.repository.*;
import com.swiftfingers.makercheckersystem.security.AuthPrincipal;
import com.swiftfingers.makercheckersystem.service.jwt.JwtTokenService;
import com.swiftfingers.makercheckersystem.service.redis.TokenService;
import com.swiftfingers.makercheckersystem.utils.MapperUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.CHANGE_PASSWORD_MSG;
import static com.swiftfingers.makercheckersystem.utils.Utils.buildResponse;
import static com.swiftfingers.makercheckersystem.utils.ValidationUtils.isValidPassword;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final AuthProvider authProvider;
    private final UserRoleRepository userRoleRepository;
    private final RoleAuthorityRepository roleAuthorityRepository;
    private final JwtTokenService tokenProvider;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryRepository historyRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    public AppResponse registerUser(SignUpRequest request) {
        return null;
    }


    public AuthenticationResponse authenticate(LoginRequest loginRequest, String sessionId) throws JsonProcessingException {
       /* Creating UsernamePasswordAuthenticationToken object to send it to authentication manager.Attention! We used two parameters
        constructor. It sets authentication false by doing this.setAuthenticated(false);
        */
        UsernamePasswordAuthenticationToken authToken = new
                UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        //we let the custom authentication manager do its work
        Authentication auth = authProvider.authenticate(authToken);
        if (auth.isAuthenticated()) {
            //Authentication succeeds
            User userAuthenticated =  (User) auth.getDetails();
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

            String authoritiesString = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));


                        //2-fa is enabled for the account
            if (userAuthenticated.is2FAEnabled()) {
                AuthPrincipal authPrincipal = new AuthPrincipal();
                authPrincipal.setAuthenticated(true);
                authPrincipal.setAuthorities(authoritiesString);
                authPrincipal.setCredentials(null);
                authPrincipal.setPrincipal((String)auth.getPrincipal());

                //generate 2FA token and send to user's token destination
                String _2faToken = tokenService.generate2FAToken(UUID.randomUUID().toString());
                String strigifiedAuthObj = MapperUtils.toJson(authPrincipal);
                String tokenPath = userAuthenticated.getTokenDestination().equals(TokenDestination.EMAIL) ? "email" : "phone number";
                Token tokenBuilder = Token.builder()
                        ._2faToken(_2faToken)
                        .creationTime(Instant.now())
                        .authPayload(strigifiedAuthObj)
                        .destination(userAuthenticated.getTokenDestination())
                        .build();

                tokenRepository.save(tokenBuilder);

                //send token to user's email or phone
                sendTokenToUser(userAuthenticated.getEmail(), _2faToken, userAuthenticated.getTokenDestination());
                return AuthenticationResponse.builder().message(String.format(SecurityMessages._2FA_TOKEN_SUCCESS,tokenPath)).build();
            }

            SecurityContextHolder.getContext().setAuthentication(auth);

            String userName = (String) auth.getPrincipal();

            JwtSubject jwtSubject = new JwtSubject(userName, authoritiesString);

            //generate a token
            String token = tokenProvider.generateToken(jwtSubject, sessionId);

            //save user token in redis
            tokenService.saveUserLoginToken(sessionId, token);


            return AuthenticationResponse.builder().token(token).authorities(authoritiesString).build();
        //      return completeAuthentication2(auth, sessionId);
        }

        if (!auth.isAuthenticated() && !ObjectUtils.isEmpty(auth.getDetails()) && auth.getDetails().equals(CHANGE_PASSWORD_MSG)) {
            return AuthenticationResponse.builder().message((String)auth.getDetails()).build();
        }

        throw new BadRequestException("User could not be authenticated");

    }

    public AppResponse changePassword(PasswordResetRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            //check user password history
            List<PasswordHistory> history = historyRepository.findPasswordsForLoginIdForLastSixMonths(user.getEmail(), LocalDateTime.now().minusMonths(6));

            if (!history.isEmpty()) {
                throw new BadRequestException(String.format("'%s' has been used before in the past 6 months. Enter a different password.", request.getNewPassword()));
            }

            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new BadRequestException("Password entered does not match user's password");
            }

            //validate password request
            if (!isValidPassword(request.getNewPassword())) {
                throw new BadRequestException("Passwords must be a minimum of 8 letters and contain a mixture of numbers," +
                        "symbols, upper and lowercase letters!");
            }

            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new BadRequestException("Password and Confirm password must match!");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            if (user.isFirstTimeLogin()) {
                user.setFirstTimeLogin(false);
                user.setActive(true);
            }

            //save user
            userRepository.save(user);

            //save password history
            PasswordHistory passHistory = PasswordHistory.builder()
                    .password(passwordEncoder.encode(request.getNewPassword()))
                    .loginId(user.getEmail())
                    .resetDate(LocalDateTime.now())
                    .build();

            passHistory.setCreatedBy(request.getEmail());
            passHistory.setUpdatedBy(request.getEmail());
            passHistory.setActive(true);
            historyRepository.save(passHistory);


            return buildResponse(HttpStatus.CREATED, "Password has been reset", null);
        }

        throw new ResourceNotFoundException(String.format("Email '%s' was not found", request.getEmail()));

    }

    public AppResponse setUp2Fa(TwoFactorAuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException(String.format(SecurityMessages.MODEL_NOT_FOUND,"User")));
        if (!user.isActive()) {
            throw new BadRequestException(String.format(SecurityMessages.MODEL_INACTIVE, "User"));
        }

        user.set2FAEnabled(authRequest.isActivate());
        user.setTokenDestination(authRequest.getDestination());
        String str = authRequest.isActivate() ? "enabled" : "disabled";
        userRepository.save(user);
        return buildResponse(HttpStatus.OK, String.format(SecurityMessages.TWO_FA_SET_UP, str), null);
    }

//    private AuthenticationResponse completeAuthentication (Authentication auth, String sessionId) {
//        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
//
//        String authoritiesString = authorities.stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//
//        //String authoritiesString =  (String) auth.getDetails();
//
//        SecurityContextHolder.getContext().setAuthentication(auth);
//
//        UserDetails principal = (UserDetails) auth.getPrincipal();
//
//        String userName = ((UserDetails) auth.getPrincipal()).getUsername();
//
//        JwtSubject jwtSubject = new JwtSubject(userName, authoritiesString);
//
//        //generate a token
//        String token = tokenProvider.generateToken(jwtSubject, sessionId);
//
//        //save user token in redis
//        tokenService.saveUserLoginToken(sessionId, token);
//
//
//        return AuthenticationResponse.builder().token(token).authorities(String.valueOf(principal.getAuthorities())).build();
//    }

    private void sendTokenToUser (String receiverEmail, String _2faToken, TokenDestination destination) {
        if (TokenDestination.EMAIL.equals(destination)) {
            //sending token using email
          emailService.sendTokenEmail(receiverEmail, _2faToken);
        } else {
            //sending token using SMS
        }
    }
}
