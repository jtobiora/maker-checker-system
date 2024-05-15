package com.swiftfingers.makercheckersystem.security;

import com.swiftfingers.makercheckersystem.enums.Message;
import com.swiftfingers.makercheckersystem.payload.JwtSubject;
import com.swiftfingers.makercheckersystem.service.jwt.JwtTokenService;
import com.swiftfingers.makercheckersystem.service.redis.LoginTokenService;
import com.swiftfingers.makercheckersystem.service.sessions.SessionManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;


import static com.swiftfingers.makercheckersystem.utils.MapperUtils.toJSON;
import static com.swiftfingers.makercheckersystem.utils.Utils.buildResponse;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenProvider;
    private final SessionManager sessionManager;
    private final LoginTokenService tokenCacheService;
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
        if (!tokenCacheService.isValidUserLoginToken(subject.getSessionId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print(toJSON(buildResponse(Message.EXPIRED_TOKEN.getValue(), HttpStatus.FORBIDDEN.value(), null)));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            return;
        }
//
        //Does the user have a valid session
        if (sessionManager.isSessionExpired(request, subject.getSessionId())) {
            tokenCacheService.deleteUserLoginToken(subject.getSessionId()); //destroy the token and remove from redis
            buildResponse(Message.EXPIRED_SESSION.getValue(), HttpStatus.FORBIDDEN.value(), null);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print(toJSON(buildResponse(Message.EXPIRED_SESSION.getValue(), HttpStatus.FORBIDDEN.value(), null)));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            return;
        }

        //continue processing
        String userName = subject.getEmail();
        Set<GrantedAuthority> grantedAuthList = new HashSet<>();
        if (!ObjectUtils.isEmpty(subject.getAuthorities())) {
            for (String per : subject.getAuthorities().split(",")) {
                grantedAuthList.add(new SimpleGrantedAuthority(per));
            }
        }

         //initializing UsernamePasswordAuthenticationToken with its 3 parameter constructor
         //because it sets super.setAuthenticated(true); in that constructor.
        UsernamePasswordAuthenticationToken upassToken =
                new UsernamePasswordAuthenticationToken(userName, null, grantedAuthList);
        upassToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // finally, give the authentication token to Spring Security Context
        SecurityContextHolder.getContext().setAuthentication(upassToken);

        filterChain.doFilter(request, response);
    }

}
