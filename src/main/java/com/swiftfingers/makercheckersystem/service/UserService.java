package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.audits.annotations.CreateOperation;
import com.swiftfingers.makercheckersystem.audits.annotations.UpdateOperation;
import com.swiftfingers.makercheckersystem.constants.SecurityMessages;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.enums.TokenDestination;
import com.swiftfingers.makercheckersystem.exceptions.AppException;
import com.swiftfingers.makercheckersystem.exceptions.ModelExistsException;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.user.QUser;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.payload.request.SignUpRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.payload.response.UserResponseDto;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import com.swiftfingers.makercheckersystem.utils.EncryptionUtil;
import com.swiftfingers.makercheckersystem.utils.MapperUtils;
import com.swiftfingers.makercheckersystem.utils.validations.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Function;

import static com.swiftfingers.makercheckersystem.constants.AppConstants.CREATE;
import static com.swiftfingers.makercheckersystem.constants.AppConstants.UPDATE;
import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.*;
import static com.swiftfingers.makercheckersystem.enums.AuthorizationStatus.INITIALIZED_CREATE;
import static com.swiftfingers.makercheckersystem.utils.GeneralUtils.buildResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailSender;
    private final EmailValidator emailValidator;
    private final PendingActionService notificationService;
    private static final String REFERENCE_TABLE = "user";

    @Value("${app.key}")
    private String key;

    @Secured("ROLE_CREATE_USER")
    @CreateOperation
    public User createUser(SignUpRequest signUpRequest, String loggedInUser) {
        log.info("Creating user with username {}.....", signUpRequest.getEmail());
        if (exists(signUpRequest, null)) {
            throw new ModelExistsException(String.format(USER_EXISTS, signUpRequest.getEmail()));
        }

        //verify that the username has not been taken
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            throw new ModelExistsException(String.format(MODEL_EXISTS, signUpRequest.getUsername()));
        }

        //verify that the email is a valid one and can receive mails
        if (!emailValidator.validateEmail(signUpRequest.getEmail()).isValid()) {
            throw new AppException(INVALID_EMAIL);
        }

        //generate password and sent to user's email address. User must change password to use the account
        String generatedPassword = PasswordManager.generatePassword();
        User user = buildUserEntity(signUpRequest, generatedPassword);
        user.setAuthorizationStatus(INITIALIZED_CREATE);
        user.setActive(false);

        User saved = userRepository.save(user);

        //send password to user's email address
        emailSender.sendPasswordEmail(signUpRequest.getEmail(), generatedPassword);

        //send message to Authorizers to approve user creation
        notificationService.sendForApprovals(CREATE, saved.getId(), loggedInUser, REFERENCE_TABLE);
        return saved;
    }

    @Secured("ROLE_EDIT_USER")
    @UpdateOperation
    public User updateUser(SignUpRequest request, Long id, String loggedInUser) {
        User userFound = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(MODEL_NOT_FOUND, "User")));
        if (exists(request, id)) {
            throw new ModelExistsException(String.format(MODEL_EXISTS, "User"));
        }

        User userToUpdate = getUser(request, userFound);

        String userInJson = MapperUtils.toJSON(userToUpdate);

        //encrypt the user json string
        String encryptedJsonUser = EncryptionUtil.encrypt(userInJson, key);

        userFound.setJsonData(encryptedJsonUser);
        userFound.setAuthorizationStatus(AuthorizationStatus.INITIALIZED_UPDATE);

        User updated =  userRepository.save(userFound);

        //send message to Authorizers to approve user update
        notificationService.sendForApprovals(UPDATE, updated.getId(), loggedInUser, REFERENCE_TABLE);

        return updated;

    }

    public AppResponse findAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
//        Page<User> mappedUser = users.map(user -> {
//            user.setPassword("###");
//            return user;
//        });
        return buildResponse(HttpStatus.OK, "All users", transformUserPage(users));
    }

    public User findById(Long id) {
       User user =  userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(SecurityMessages.MODEL_NOT_FOUND, "User")));
       user.setPassword("###");
       return user;
    }

    public boolean exists(SignUpRequest request, Long id) {
        if (id == null) {
            return userRepository.findByEmail(request.getEmail()).isPresent();
        } else {
            return userRepository.findUserByEmailAndIdNot(request.getEmail(), id).isPresent();
        }
    }

    public Page<UserResponseDto> search (User user, PageRequest p) {
        QUser qUser = QUser.user;
        if (ObjectUtils.isEmpty(user)) {
            Page<User> users=  userRepository.findAll(qUser.isNotNull(), p);
            return transformUserPage(users);
        }
        Page<User> u = userRepository.findAll(user.predicates(), p);
        return  transformUserPage(u);
    }

    private User buildUserEntity(SignUpRequest signUpRequest, String generatedPassword) {
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

    private static User getUser(SignUpRequest request, User userFound) {
        User userToUpdate = new User();
        userToUpdate.setId(userFound.getId());
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
        return userToUpdate;
    }

    public Page<UserResponseDto> transformUserPage (Page<User> userPage) {

        Function<User, UserResponseDto> mapper = user -> UserResponseDto.builder()
                .id(user.getId())
                .active(user.isActive())
                .authorizationStatus(user.getAuthorizationStatus())
                .isFirstTimeLogin(user.isFirstTimeLogin())
                .lastName(user.getLastName())
                .loginAttempt(user.getLoginAttempt())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .username(user.getUsername())
                .tokenDestination(user.getTokenDestination())
                .is2FAEnabled(user.is2FAEnabled())
                .build();

        return userPage.map(mapper);
    }

}
