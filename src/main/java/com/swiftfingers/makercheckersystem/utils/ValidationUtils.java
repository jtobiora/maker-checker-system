package com.swiftfingers.makercheckersystem.utils;

import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.payload.request.PasswordResetRequest;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.swiftfingers.makercheckersystem.constants.AppConstants.PASSWORD_PATTERN;
import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.*;

@Component
@Data
public class ValidationUtils {

    // Method to validate password
    public static boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static void validatePassword (PasswordResetRequest request, User user, PasswordEncoder passwordEncoder) {
        //check if user old password and passed password are same
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException(PASSWORD_MISMATCH_ERR);
        }

        //check if password meets validation rules
        if (!isValidPassword(request.getNewPassword())) {
            throw new BadRequestException(PASSWORD_RULE_MSG);
        }

        //check new and confirm password if they match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(PASSWORD_CONFIRM_PASS_MISMATCH_ERR);
        }
    }


}
