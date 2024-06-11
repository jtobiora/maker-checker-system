package com.swiftfingers.makercheckersystem.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class GeneralUtils {

    public static Boolean getBoolean(Boolean value) {
        return ObjectUtils.isEmpty(value) ? Boolean.FALSE : value;
    }
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static AppResponse buildResponse(HttpStatus httpStatus, String message, Object data) {
        return AppResponse.builder()
                .message(message)
                .data(data)
                .status(httpStatus.toString())
                .build();
    }

    public static AppResponse buildResponse (String message, int responseCode, List<String> errors) {
        return AppResponse.builder()
                .message(message)
                .responseCode(responseCode)
                .errors(errors)
                .build();
    }

    public static String nullSafeString(String str) {
        return StringUtils.isEmpty(str) ? "" : str.trim();
    }

}
