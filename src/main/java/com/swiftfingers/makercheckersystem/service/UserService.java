package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.constants.SecurityMessages;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.enums.TokenDestination;
import com.swiftfingers.makercheckersystem.exceptions.AppException;
import com.swiftfingers.makercheckersystem.exceptions.ModelExistsException;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.payload.request.SignUpRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import com.swiftfingers.makercheckersystem.utils.MapperUtils;
import com.swiftfingers.makercheckersystem.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.*;
import static com.swiftfingers.makercheckersystem.enums.AuthorizationStatus.INITIALIZED_CREATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Secured("ROLE_CREATE_USER")
    public User createUser (SignUpRequest signUpRequest) {
        log.info("Creating user with username {}.....", signUpRequest.getEmail());
        if (exists(signUpRequest, null)) {
            throw new ModelExistsException(String.format(USER_EXISTS, signUpRequest.getEmail()));
        }

        //verify that the username has not been taken
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            throw new ModelExistsException(String.format(MODEL_EXISTS, signUpRequest.getUsername()));
        }

        //verify that the email is a valid one and can receive mails
        if (!emailService.isValidEmail(signUpRequest.getEmail())) {
            throw new AppException(INVALID_EMAIL);
        }

        //generate password and sent to user's email address. User must change password to use the account
        String generatedPassword = PasswordManager.generatePassword();
        User user = buildUserEntity(signUpRequest, generatedPassword);
        user.setAuthorizationStatus(INITIALIZED_CREATE);
        user.setActive(false);

        User saved = userRepository.save(user);

        //send password to user's email address
        emailService.sendPasswordEmail(signUpRequest.getEmail(), generatedPassword);
        return saved;
    }

    @Secured("ROLE_CREATE_USER")
    public User updateUser (SignUpRequest request, Long id) {
        User userFound  = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(MODEL_NOT_FOUND,"User")));
        if (exists(request, id)) {
            throw new ModelExistsException(String.format(MODEL_EXISTS,"User"));
        }

//        User userToUpdate = User.builder()
//                .password(userFound.getPassword())
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .email(userFound.getEmail())
//                .username(userFound.getUsername())
//                .tokenDestination(request.getTokenDestination())
//                .is2FAEnabled(userFound.is2FAEnabled())
//                .firstTimeLogin(userFound.isFirstTimeLogin())
//                .loginAttempt(userFound.getLoginAttempt())
//                .phoneNumber(request.getPhoneNumber())
//                .build();
//
//        userToUpdate.setActive(userFound.isActive());
    //    userToUpdate.setAuthorizationStatus(AUTHORIZED);

        User userToUpdate = new User();
        userToUpdate.setPassword(userFound.getPassword());
        userToUpdate.setFirstName(request.getFirstName());
        userToUpdate.setLastName(request.getLastName());
        userToUpdate.setEmail(userFound.getEmail());
        userToUpdate.setUsername(userFound.getUsername());
        userToUpdate.setTokenDestination(request.getTokenDestination());
        userToUpdate.set2FAEnabled(userFound.is2FAEnabled());
        userToUpdate.setFirstTimeLogin(userFound.isFirstTimeLogin());
        userToUpdate.setLoginAttempt(userFound.getLoginAttempt());
        userToUpdate.setPhoneNumber(request.getPhoneNumber());
        userToUpdate.setActive(true);

        String stringifiedUser = MapperUtils.toJSON(userToUpdate);
        System.out.println(stringifiedUser);

        userFound.setJsonData(stringifiedUser);
        userFound.setAuthorizationStatus(AuthorizationStatus.INITIALIZED_UPDATE);

        return userRepository.save(userFound);

    }
    public AppResponse findAllUsers (Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return Utils.buildResponse(HttpStatus.OK, "All users", users);
    }

    public User findById (Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(SecurityMessages.MODEL_NOT_FOUND,"User")));
    }

    public boolean exists(SignUpRequest request, Long id) {
        if (id == null) {
            return userRepository.findByEmail(request.getEmail()).isPresent();
        } else {
            return userRepository.findUserByEmailAndIdNot(request.getEmail(), id).isPresent();
        }
    }

    private User buildUserEntity (SignUpRequest signUpRequest, String generatedPassword) {
        User user = User.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .loginAttempt(0)
                .email(signUpRequest.getEmail())
                .username(signUpRequest.getUsername())
                .isFirstTimeLogin(true)
                .is2FAEnabled(false)
                .tokenDestination(TokenDestination.EMAIL)
                .password(passwordEncoder.encode(generatedPassword))
                .build();
        return user;
    }


}
