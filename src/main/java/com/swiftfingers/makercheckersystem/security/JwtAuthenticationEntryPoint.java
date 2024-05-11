package com.swiftfingers.makercheckersystem.security;

import com.swiftfingers.makercheckersystem.enums.Message;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.swiftfingers.makercheckersystem.utils.MapperUtils.toJson;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        log.error("User is unauthorized to perform action. Message - {}", e.getMessage());

        AppResponse appResponse = buildResponse(Message.UNAUTHORIZED.getValue(), HttpStatus.UNAUTHORIZED.value(), null);
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.getWriter().print(toJson(appResponse));
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
