package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.constants.SecurityMessages;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.enums.TokenDestination;
import com.swiftfingers.makercheckersystem.exceptions.AppException;
import com.swiftfingers.makercheckersystem.exceptions.ModelExistsException;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.model.roleauthority.RoleAuthority;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.payload.request.SignUpRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.repository.RoleAuthorityRepository;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import com.swiftfingers.makercheckersystem.repository.UserRoleRepository;
import com.swiftfingers.makercheckersystem.utils.EncryptionUtil;
import com.swiftfingers.makercheckersystem.utils.MapperUtils;
import com.swiftfingers.makercheckersystem.utils.GeneralUtils;
import com.swiftfingers.makercheckersystem.utils.validations.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.*;
import static com.swiftfingers.makercheckersystem.enums.AuthorizationStatus.INITIALIZED_CREATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailSender;
    private final EmailValidator emailValidator;
    private final NotificationService notificationService;
    private final PermissionService permissionService;
    private final RoleAuthorityRepository roleAuthorityRepository;
    private final UserRoleRepository userRoleRepository;

    @Value("${app.key}")
    private String key;

    @Secured("ROLE_CREATE_USER")
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

        Optional<User> adminAuthorizer = getAdminAuthorizer();
        User adminAuthUser = adminAuthorizer.orElseGet(User::new);

        //send message to Authorizers to approve account creation
        notificationService.sendApprovalNotification("CREATE", 1L, adminAuthUser.getEmail(), loggedInUser, "user");

        return saved;
    }

    @Secured("ROLE_CREATE_USER")
    public User updateUser(SignUpRequest request, Long id) {
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

        return userRepository.save(userFound);

    }

    private static User getUser(SignUpRequest request, User userFound) {
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
        return userToUpdate;
    }

    public AppResponse findAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return GeneralUtils.buildResponse(HttpStatus.OK, "All users", users);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(SecurityMessages.MODEL_NOT_FOUND, "User")));
    }

    public boolean exists(SignUpRequest request, Long id) {
        if (id == null) {
            return userRepository.findByEmail(request.getEmail()).isPresent();
        } else {
            return userRepository.findUserByEmailAndIdNot(request.getEmail(), id).isPresent();
        }
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

    public Optional<User> getAdminAuthorizer() {
        List<Permission> allPermissions = permissionService.getAllPermissions();
        if (!ObjectUtils.isEmpty(allPermissions)) {
            List<RoleAuthority> roleAuthority = roleAuthorityRepository.findByPermissionCodes(allPermissions.stream().map(Permission::getCode).filter(code -> code.startsWith("APPROVE")).toList());
            List<Long> roleIds = roleAuthority.stream().map(r -> r.getRole().getId()).toList();
            return userRoleRepository.findAllUsersByRole(roleIds).stream().findAny();
        }
        return Optional.empty();
    }

}
