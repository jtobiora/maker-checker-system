package com.swiftfingers.makercheckersystem.constants;

public class SecurityMessages {

    public static final String CHANGE_PASSWORD_MSG = "User is expected to change password to continue.";
    public static final String TOKEN_SUBJECT = "Verification Token";

    public static final String MODEL_NOT_FOUND = "'%s' not found";

    public static final String MODEL_EXISTS = "'%s' already exists";
    public static final String MODEL_INACTIVE = "%s is not active. Activate to continue!";
    public static final String TWO_FA_SET_UP = "Two factor authentication has been %s for this account.";

    //Token Messages
    public static final String _2FA_TOKEN_TIMEOUT = "";
    public static final String _2FA_TOKEN_ERR = "The token entered is incorrect.";
    public static final String _2FA_TOKEN_SUCCESS= "Enter the token sent to your %s";
    public static final String _2FA_TOKEN_EXPIRED_ERR = "Token has expired and is invalid";

    //password Messages
    public static final String INVALID_PASSWORD_MSG = "Token has expired and is invalid";
    public static final String PASSWORD_HISTORY_MSG = "Password has been used before in the past 6 months. Enter a different password.";

    public static final String PASSWORD_MISMATCH_ERR = "Password entered does not match user's password";
    public static final String PASSWORD_RULE_MSG = "Passwords must be a minimum of 8 letters and contain a mixture of numbers," +
            "symbols, upper and lowercase letters!";
    public static final String PASSWORD_CONFIRM_PASS_MISMATCH_ERR = "Password and Confirm password do not match!";
}
