package com.swiftfingers.makercheckersystem.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailValidatorResponse {
    private boolean isValid;
    private String message;
}
