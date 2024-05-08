package com.swiftfingers.makercheckersystem.security;

import com.swiftfingers.makercheckersystem.enums.Message;
import com.swiftfingers.makercheckersystem.payload.JwtSubject;
import com.swiftfingers.makercheckersystem.service.AuthenticationService;
import com.swiftfingers.makercheckersystem.service.jwt.JwtTokenService;
import com.swiftfingers.makercheckersystem.service.redis.TokenCacheService;
import com.swiftfingers.makercheckersystem.service.sessions.SessionManager;
import com.swiftfingers.makercheckersystem.utils.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.swiftfingers.makercheckersystem.utils.Utils.buildResponse;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationService authService;
    private final SessionManager sessionManager;
    private final TokenCacheService tokenCacheService;
    private final JwtTokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authToken = tokenService.getJwtFromRequest(request);
        JwtSubject subject = tokenProvider.getDetailsFromToken(authToken);

        //is the user authenticated?
        if (subject == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //is the user token valid
        if (!tokenCacheService.isValidUserToken(subject.getSessionId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print(Utils.toJson(buildResponse(Message.EXPIRED_TOKEN.getValue(), HttpStatus.FORBIDDEN.value(), null)));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            return;
        }

        //Does the user have a valid session
        if (sessionManager.isSessionExpired(request)) {
            tokenCacheService.deleteUserToken(subject.getSessionId()); //destroy the token and remove from redis
            buildResponse(Message.EXPIRED_SESSION.getValue(), HttpStatus.FORBIDDEN.value(), null);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print(Utils.toJson(buildResponse(Message.EXPIRED_SESSION.getValue(), HttpStatus.FORBIDDEN.value(), null)));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            return;
        }

        //continue processing
        AuthPrincipal auth = new AuthPrincipal();
        Set<GrantedAuthority> galist = new HashSet<>();
        if (!ObjectUtils.isEmpty(subject.getAuthorities())) {
            for (String per : subject.getAuthorities().split(",")) {
                galist.add(new SimpleGrantedAuthority(per));
            }
        }

        authService.recreateAuthentication(auth, authToken, galist);

        filterChain.doFilter(request, response);
    }
}
