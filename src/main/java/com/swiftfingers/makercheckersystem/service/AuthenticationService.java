package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.user.PasswordHistory;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.payload.JwtSubject;
import com.swiftfingers.makercheckersystem.payload.request.LoginRequest;
import com.swiftfingers.makercheckersystem.payload.request.PasswordResetRequest;
import com.swiftfingers.makercheckersystem.payload.request.SignUpRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.payload.response.AuthenticationResponse;
import com.swiftfingers.makercheckersystem.repository.PasswordHistoryRepository;
import com.swiftfingers.makercheckersystem.repository.RoleAuthorityRepository;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import com.swiftfingers.makercheckersystem.repository.UserRoleRepository;
import com.swiftfingers.makercheckersystem.security.AuthPrincipal;
import com.swiftfingers.makercheckersystem.service.jwt.JwtTokenService;
import com.swiftfingers.makercheckersystem.service.redis.TokenCacheService;
import com.swiftfingers.makercheckersystem.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private final TokenCacheService tokenCacheService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryRepository historyRepository;

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

            //save user token in redis
            tokenCacheService.saveUserToken(sessionId, token);


            return AuthenticationResponse.builder().token(token).authorities(String.valueOf(principal.getAuthorities())).build();
        }

        if (!auth.isAuthenticated() && !ObjectUtils.isEmpty(auth.getDetails())) {
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
}
