package com.swiftfingers.makercheckersystem.security;

import com.google.gson.Gson;
import com.swiftfingers.makercheckersystem.enums.Errors;
import com.swiftfingers.makercheckersystem.exceptions.ErrorDetails;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        log.error("User is unauthorized to perform action. Message - {}", e.getMessage());

        AppResponse appResponse = buildResponse(Errors.UNAUTHORIZED.getValue(), HttpStatus.UNAUTHORIZED.value(), null);
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.getWriter().print(Utils.toJson(appResponse));
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setHeader("Access-Control-Allow-Origin",httpServletRequest.getHeader("Origin"));
    }

    private AppResponse buildResponse (String message, int responseCode,List<String> errors) {
        return AppResponse.builder()
                .message(message)
                //.timestamp(LocalDateTime.now())
                .responseCode(responseCode)
                .errors(errors)
                .build();
    }
}
