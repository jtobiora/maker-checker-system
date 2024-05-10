package com.swiftfingers.makercheckersystem.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import java.util.List;

public class Utils {

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

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }
    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}
