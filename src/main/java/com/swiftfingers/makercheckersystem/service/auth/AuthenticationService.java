package com.swiftfingers.makercheckersystem.service.auth;

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
import com.swiftfingers.makercheckersystem.repository.PasswordHistoryRepository;
import com.swiftfingers.makercheckersystem.repository.TokenRepository;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import com.swiftfingers.makercheckersystem.security.AuthPrincipal;
import com.swiftfingers.makercheckersystem.service.AuthProvider;
import com.swiftfingers.makercheckersystem.service.EmailSender;
import com.swiftfingers.makercheckersystem.service.TwoFaTokenService;
import com.swiftfingers.makercheckersystem.service.jwt.JwtTokenService;
import com.swiftfingers.makercheckersystem.service.redis.LoginTokenService;
import com.swiftfingers.makercheckersystem.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.*;
import static com.swiftfingers.makercheckersystem.utils.MapperUtils.fromJSON;
import static com.swiftfingers.makercheckersystem.utils.MapperUtils.toJSON;
import static com.swiftfingers.makercheckersystem.utils.Utils.buildResponse;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {

    private final AuthProvider authProvider;
    private final JwtTokenService tokenProvider;
    private final LoginTokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryRepository historyRepository;
    private final TokenRepository tokenRepository;
    private final EmailSender emailService;
    private final TwoFaTokenService twoFaTokenService;

    public AppResponse registerUser(SignUpRequest request) {
        return null;
    }


//    public AuthenticationResponse authenticate(LoginRequest loginRequest, String sessionId)  {
//
//        UsernamePasswordAuthenticationToken authToken = new
//                UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
//
//        //we let the custom authentication manager do its work
//        Authentication auth = authProvider.authenticate(authToken);
//
//        if (auth.isAuthenticated()) {
//            //Authentication succeeds
//            User userAuthenticated =  (User) auth.getDetails();
//            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
//
//            String authoritiesString = authorities.stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .collect(Collectors.joining(","));
//
//
//                        //2-fa is enabled for the account
//            if (userAuthenticated.is2FAEnabled()) {
//                AuthPrincipal authPrincipal = new AuthPrincipal();
//                authPrincipal.setAuthenticated(true);
//                authPrincipal.setAuthorities(authoritiesString);
//                authPrincipal.setCredentials(null);
//                authPrincipal.setPrincipal((String)auth.getPrincipal());
//
//                //generate 2FA token and send to user's token destination
//                String identifier = UUID.randomUUID().toString();
//                String strigifiedAuthObj = toJSON(authPrincipal);
//                String tokenPath = userAuthenticated.getTokenDestination().equals(TokenDestination.EMAIL) ? "email" : "phone number";
//                String _2faToken = twoFaTokenService.generateAndSaveToken(userAuthenticated.getEmail(),userAuthenticated.getTokenDestination(),identifier,strigifiedAuthObj);
//
//
//                //send token to user's email or phone
//                sendTokenToUser(userAuthenticated.getEmail(), _2faToken, userAuthenticated.getTokenDestination());
//                return AuthenticationResponse.builder().message(String.format(SecurityMessages._2FA_TOKEN_SUCCESS,tokenPath)).build();
//            }
//
//            SecurityContextHolder.getContext().setAuthentication(auth);
//
//            String userName = (String) auth.getPrincipal();
//
//            JwtSubject jwtSubject = new JwtSubject(userName, authoritiesString);
//
//            //generate a token
//            String token = tokenProvider.generateToken(jwtSubject, sessionId);
//
//            //save user token in redis
//            tokenService.saveUserLoginToken(sessionId, token);
//
//
//            return AuthenticationResponse.builder().token(token).authorities(authoritiesString).build();
//        //      return completeAuthentication2(auth, sessionId);
//        }
//
//        if (!auth.isAuthenticated() && !ObjectUtils.isEmpty(auth.getDetails()) && auth.getDetails().equals(CHANGE_PASSWORD_MSG)) {
//            return AuthenticationResponse.builder().message((String)auth.getDetails()).build();
//        }
//
//        throw new BadRequestException("User could not be authenticated");
//
//    }
//
//    public AppResponse changePassword(PasswordResetRequest request) {
//        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            //check user password history
//            List<PasswordHistory> history = historyRepository.findPasswordsForLoginIdForLastSixMonths(user.getEmail(), LocalDateTime.now().minusMonths(6));
//
//            if (!history.isEmpty()) {
//                throw new BadRequestException(PASSWORD_HISTORY_MSG);
//            }
//
//            ValidationUtils.validatePassword(request, user,passwordEncoder);
//
//            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//            if (user.isFirstTimeLogin()) {
//                user.setFirstTimeLogin(false);
//                user.setActive(true);
//            }
//
//            //save user
//            userRepository.save(user);
//
//            //save password history
//            PasswordHistory passHistory = PasswordHistory.builder()
//                    .password(passwordEncoder.encode(request.getNewPassword()))
//                    .loginId(user.getEmail())
//                    .resetDate(LocalDateTime.now())
//                    .build();
//
//            passHistory.setCreatedBy(request.getEmail());
//            passHistory.setUpdatedBy(request.getEmail());
//            passHistory.setActive(true);
//            historyRepository.save(passHistory);
//
//
//            return buildResponse(HttpStatus.CREATED, "Password has been reset", null);
//        }
//
//        throw new ResourceNotFoundException(String.format("Email '%s' was not found", request.getEmail()));
//
//    }
//
//    public AppResponse setUp2Fa(TwoFactorAuthRequest authRequest) {
//        User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException(String.format(SecurityMessages.MODEL_NOT_FOUND,"User")));
//        if (!user.isActive()) {
//            throw new BadRequestException(String.format(SecurityMessages.MODEL_INACTIVE, "User"));
//        }
//
//        user.set2FAEnabled(authRequest.isActivate());
//        user.setTokenDestination(authRequest.getDestination());
//        String str = authRequest.isActivate() ? "enabled" : "disabled";
//        userRepository.save(user);
//        return buildResponse(HttpStatus.OK, String.format(SecurityMessages.TWO_FA_SET_UP, str), null);
//    }
//
//    private void sendTokenToUser (String receiverEmail, String _2faToken, TokenDestination destination) {
//        if (TokenDestination.EMAIL.equals(destination)) {
//            //sending token using email
//          emailService.sendTokenEmail(receiverEmail, _2faToken);
//        } else {
//            //sending token using SMS
//        }
//    }
//
//    public AuthenticationResponse confirm2FaToken(String tokenPassed, String email, String sessionId) {
//        User userFound = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(String.format(MODEL_NOT_FOUND,"User")));
//
//        List<Token> tokens = tokenRepository.findTokensByLoginId(userFound.getEmail());
//
//        if(tokens.isEmpty()) {
//            throw new BadRequestException(_2FA_TOKEN_ERR);
//        }
//
//        Token composedToken = null;
//        for (Token toks : tokens) {
//            if (passwordEncoder.matches(tokenPassed, toks.get_2faToken())) {
//                //check if the token has expired or is invalid
//                if(!twoFaTokenService.is2FATokenValid(toks, tokenPassed)) {
//                    throw new BadRequestException(_2FA_TOKEN_EXPIRED_ERR);
//                } else {
//                    composedToken = toks;
//                    break;
//                }
//            }
//        }
//
//        if (!ObjectUtils.isEmpty(composedToken)) {
//            log.info("Authenticate the user and set information in the Security Context");
//            AuthPrincipal authPrincipal = fromJSON(composedToken.getAuthPayload(), AuthPrincipal.class);
//            Set<GrantedAuthority> grantedAuthList = new HashSet<>();
//            if (!ObjectUtils.isEmpty(authPrincipal.getAuthorities())) {
//                for (String per : authPrincipal.getAuthorities().split(",")) {
//                    grantedAuthList.add(new SimpleGrantedAuthority(per));
//                }
//            }
//
//            Authentication authentication = new UsernamePasswordAuthenticationToken(authPrincipal.getPrincipal(),authPrincipal.getCredentials(),grantedAuthList);
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            String userName = authPrincipal.getPrincipal();
//
//            JwtSubject jwtSubject = new JwtSubject(userName, authPrincipal.getAuthorities());
//
//            //generate a token
//            String token = tokenProvider.generateToken(jwtSubject, sessionId);
//
//            //save user token in redis
//            tokenService.saveUserLoginToken(sessionId, token);
//
//            return AuthenticationResponse.builder().token(token).authorities(authPrincipal.getAuthorities()).build();
//        }
//
//        return null;
//    }
//
//    private void completeAuthentication () {
//
//    }


    //############################################ REFACTORED ################################
    public AuthenticationResponse authenticate(LoginRequest loginRequest, String sessionId) {
        Authentication auth = authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

        if (auth.isAuthenticated()) {
            User user = (User) auth.getDetails();
            if (user.is2FAEnabled()) {
                return handleTwoFactorAuth(auth, user, sessionId);
            } else {
                return handleRegularAuth(auth, sessionId);
            }
        }

        if (!auth.isAuthenticated() && !ObjectUtils.isEmpty(auth.getDetails()) && auth.getDetails().equals(CHANGE_PASSWORD_MSG)) {
            return AuthenticationResponse.builder().message((String) auth.getDetails()).build();
        }

        throw new BadRequestException("User could not be authenticated");
    }

    private Authentication authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        return authProvider.authenticate(authToken);
    }

    private AuthenticationResponse handleTwoFactorAuth(Authentication auth, User user, String sessionId) {
        // 2FA is enabled
        AuthPrincipal authPrincipal = createAuthPrincipal(auth);

        String identifier = UUID.randomUUID().toString();
        String strigifiedAuthObj = toJSON(authPrincipal);
        String tokenPath = user.getTokenDestination().equals(TokenDestination.EMAIL) ? "email" : "phone number";
        String _2faToken = twoFaTokenService.generateAndSaveToken(user.getEmail(), user.getTokenDestination(), identifier, strigifiedAuthObj);

        sendTokenToUser(user.getEmail(), _2faToken, user.getTokenDestination());

        return AuthenticationResponse.builder().message(String.format(SecurityMessages._2FA_TOKEN_SUCCESS, tokenPath)).build();
    }

    private AuthenticationResponse handleRegularAuth(Authentication auth, String sessionId) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String authoritiesString = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        SecurityContextHolder.getContext().setAuthentication(auth);
        String userName = (String) auth.getPrincipal();
        JwtSubject jwtSubject = new JwtSubject(userName, authoritiesString);
        String token = tokenProvider.generateToken(jwtSubject, sessionId);
        tokenService.saveUserLoginToken(sessionId, token);

        return AuthenticationResponse.builder().token(token).authorities(authoritiesString).build();
    }

    private AuthPrincipal createAuthPrincipal(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String authoritiesString = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        AuthPrincipal authPrincipal = new AuthPrincipal();
        authPrincipal.setAuthenticated(true);
        authPrincipal.setAuthorities(authoritiesString);
        authPrincipal.setCredentials(null);
        authPrincipal.setPrincipal((String) auth.getPrincipal());
        return authPrincipal;
    }

    private void sendTokenToUser(String receiverEmail, String _2faToken, TokenDestination destination) {
        if (TokenDestination.EMAIL.equals(destination)) {
            //sending token using email
            emailService.sendTokenEmail(receiverEmail, _2faToken);
        } else {
            //sending token using SMS
        }
    }

    public AuthenticationResponse confirm2FaToken(String tokenPassed, String email, String sessionId) {
        User userFound = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(String.format(MODEL_NOT_FOUND, "User")));
        Token composedToken = getTokenForUser(tokenPassed, userFound);

        if (!ObjectUtils.isEmpty(composedToken)) {
            return authenticateUserFromToken(composedToken, sessionId);
        }

        return null;
    }

    private Token getTokenForUser(String tokenPassed, User user) {
        List<Token> tokens = tokenRepository.findTokensByLoginId(user.getEmail());
        return tokens.stream()
                .filter(t -> passwordEncoder.matches(tokenPassed, t.get_2faToken()) && twoFaTokenService.is2FATokenValid(t, tokenPassed))
                .findFirst()
                .orElse(null);
    }

    private AuthenticationResponse authenticateUserFromToken(Token token, String sessionId) {
        AuthPrincipal authPrincipal = fromJSON(token.getAuthPayload(), AuthPrincipal.class);
        Authentication authentication = createAuthenticationFromPrincipal(authPrincipal);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return handleRegularAuth(authentication, sessionId);
    }

    private Authentication createAuthenticationFromPrincipal(AuthPrincipal authPrincipal) {
        Set<GrantedAuthority> grantedAuthList = Arrays.stream(authPrincipal.getAuthorities().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new UsernamePasswordAuthenticationToken(authPrincipal.getPrincipal(), authPrincipal.getCredentials(), grantedAuthList);
    }

    public AppResponse setUp2Fa(TwoFactorAuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException(String.format(SecurityMessages.MODEL_NOT_FOUND, "User")));
        if (!user.isActive()) {
            throw new BadRequestException(String.format(SecurityMessages.MODEL_INACTIVE, "User"));
        }

        user.set2FAEnabled(authRequest.isActivate());
        user.setTokenDestination(authRequest.getDestination());
        String str = authRequest.isActivate() ? "enabled" : "disabled";
        userRepository.save(user);
        return buildResponse(HttpStatus.OK, String.format(SecurityMessages.TWO_FA_SET_UP, str), null);
    }

    public AppResponse changePassword(PasswordResetRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            //check user password history
            List<PasswordHistory> history = historyRepository.findPasswordsForLoginIdForLastSixMonths(user.getEmail(), LocalDateTime.now().minusMonths(6));

            if (!history.isEmpty()) {
                throw new BadRequestException(PASSWORD_HISTORY_MSG);
            }

            ValidationUtils.validatePassword(request, user, passwordEncoder);

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            if (user.isFirstTimeLogin()) {
                user.setFirstTimeLogin(false);
                user.setActive(true);
            }

            //save user
            userRepository.save(user);

            buildPasswordHistory(user, request);

            return buildResponse(HttpStatus.CREATED, "Password has been reset", null);
        }

        throw new ResourceNotFoundException(String.format("Email '%s' was not found", request.getEmail()));

    }

    private void buildPasswordHistory (User user, PasswordResetRequest request) {
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
    }

}
