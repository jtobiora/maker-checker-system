package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.repository.RoleAuthorityRepository;
import com.swiftfingers.makercheckersystem.repository.RoleRepository;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import com.swiftfingers.makercheckersystem.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.*;
import static com.swiftfingers.makercheckersystem.enums.AuthorizationStatus.AUTHORIZED;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordManager passwordManager;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleAuthorityRepository roleAuthorityRepository;


    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth.isAuthenticated()) {
            log.info("Already authenticated. return same");
            return auth;
        }

        UsernamePasswordAuthenticationToken authenticationToken = null;
        String username = auth.getName();
        String password = String.valueOf(auth.getCredentials());
        User userFound = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException(String.format(MODEL_NOT_FOUND,"User")));

        //first time login user -- prompt for password change
        if (userFound.isFirstTimeLogin()) {
            authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            authenticationToken.setDetails(CHANGE_PASSWORD_MSG);

            return authenticationToken;
        }

        //check if user has been authorized
        if (!userFound.isActive() || userFound.getAuthorizationStatus() != AUTHORIZED) {
            throw new BadRequestException(USER_NOT_ACTIVE_OR_AUTHORIZED);
        }
        passwordManager.checkPassword(password, userFound);

        Map<String, Object> authMap = getGrantedAuthorities(userFound);
        Collection<GrantedAuthority> grantedAuthorities = (Collection<GrantedAuthority>) authMap.get("grantedAuth");

        authenticationToken = new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities);

        authenticationToken.setDetails(userFound);
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return UsernamePasswordAuthenticationToken.class.equals(authenticationType);
    }

    private Map<String, Object> getGrantedAuthorities(User userFound) {
        Map<String, Object> objectMap = new HashMap<>();
        List<Role> roles = userRoleRepository.findAllRolesByUserId(userFound.getId(), AUTHORIZED);

        Set<String> permissionCodeList = new HashSet<>();
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        // load user Authorities into GrantedAuthority
        if (!ObjectUtils.isEmpty(roles)) {
            for (Role role : roles) {
                List<Permission> authorities = roleAuthorityRepository.findAllPermissionsByRoleId(role.getId(), AUTHORIZED);
                for (Permission permission : authorities) {
                    permissionCodeList.add(permission.getCode());
                    grantedAuthorities.add(new SimpleGrantedAuthority(permission.getCode()));
                }
            }
        }

        String authorities = permissionCodeList.isEmpty() ? "" : String.join(",", permissionCodeList);

        objectMap.put("authString", authorities);
        objectMap.put("grantedAuth", grantedAuthorities);
        return objectMap;
    }

}
