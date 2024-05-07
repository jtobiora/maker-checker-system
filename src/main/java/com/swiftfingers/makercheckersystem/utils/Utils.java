package com.swiftfingers.makercheckersystem.utils;

import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import org.springframework.http.HttpStatus;

public class Utils {

    public static AppResponse buildAppResponse(HttpStatus httpStatus, String message, Object data) {
        return AppResponse.builder()
                .message(message)
                .data(data)
                .status(httpStatus.toString())
                .build();
    }
}
