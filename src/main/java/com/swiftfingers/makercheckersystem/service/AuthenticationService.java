package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.model.user.User;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthProvider authProvider;
    private final UserRoleRepository userRoleRepository;
    private final RoleAuthorityRepository roleAuthorityRepository;
    private final JwtTokenService tokenProvider;
    private final TokenCacheService tokenCacheService;

   public AppResponse registerUser (SignUpRequest request) {
       return null;
   }


   public AuthenticationResponse authenticate (LoginRequest loginRequest, String sessionId) {

       //Authenticate the user request - email and password
       Authentication authentication = authProvider.authenticate(
               new UsernamePasswordAuthenticationToken(
                       loginRequest.getEmail(),
                       loginRequest.getPassword()
               )
       );

       AuthPrincipal authPrincipal = new AuthPrincipal();
       User userFound = (User)authentication.getPrincipal();
       List<Role> roles = userRoleRepository.findAllRolesByUserId(userFound.getId());

       Set<String> permissionCodeList = new HashSet<>();
       Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

       // load user Authorities into GrantedAuthority
       if (!ObjectUtils.isEmpty(roles)) {
           for (Role role : roles) {
               List<Permission> authorities = roleAuthorityRepository.findAllPermissionsByRoleId(role.getId());
               for (Permission permission : authorities) {
                   permissionCodeList.add(permission.getCode());
                   grantedAuthorities.add(new SimpleGrantedAuthority(permission.getCode()));
               }
           }
       }

       String authorities = permissionCodeList.isEmpty() ? "" : String.join(",", permissionCodeList);

       JwtSubject jwtSubject = new JwtSubject(userFound.getEmail(), authorities);

       //generate a token
       String token = tokenProvider.generateToken(jwtSubject, sessionId);

       //start a session in redis
       tokenCacheService.saveUserToken(sessionId, token);

       //set user as logged in
       tokenCacheService.setUserAsLogged(userFound.getEmail(), sessionId);

       recreateAuthentication(authPrincipal, token, grantedAuthorities);

       return AuthenticationResponse.builder().auth(authPrincipal).token(token).authorities(authorities).build();
   }

    public void recreateAuthentication(AuthPrincipal auth, String token, Set<GrantedAuthority> authorities) {
        auth.setAuthorities(authorities);
        auth.setToken(token);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
