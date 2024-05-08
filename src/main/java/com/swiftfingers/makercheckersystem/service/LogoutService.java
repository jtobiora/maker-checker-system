package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.enums.Message;
import com.swiftfingers.makercheckersystem.payload.JwtSubject;
import com.swiftfingers.makercheckersystem.service.jwt.JwtTokenService;
import com.swiftfingers.makercheckersystem.service.redis.TokenCacheService;
import com.swiftfingers.makercheckersystem.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.swiftfingers.makercheckersystem.utils.Utils.buildResponse;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtTokenService tokenService;
    private final TokenCacheService tokenCacheService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        final String token = authHeader.substring(7);
        JwtSubject subject = tokenService.getDetailsFromToken(token);
        // Clear the security context
        SecurityContextHolder.clearContext();

        //remove the token
        tokenCacheService.deleteUserToken(subject.getSessionId()); //destroy the token and remove from redis

        // Invalidate the session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        response.setStatus(HttpServletResponse.SC_OK);
        try {
            response.getWriter().print(Utils.toJson(buildResponse(Message.LOGOUT_MSG.getValue(), HttpStatus.OK.value(), null)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
    }
}