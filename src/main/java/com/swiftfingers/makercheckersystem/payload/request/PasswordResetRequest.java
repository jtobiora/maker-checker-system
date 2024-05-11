package com.swiftfingers.makercheckersystem.payload.request;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
