package com.swiftfingers.makercheckersystem.enums;

public enum Message {
    EXPIRED_SESSION("Your Session has expired."),
    EXPIRED_TOKEN("The token has expired."),
    UNKNOWN_USER("The user is not known."),
    UNAUTHORIZED("User has not been authorized!."),
    NOT_PERMITTED("Permission not granted."),

    LOGOUT_MSG("User is successfully logged out.");

    private String value;

    private Message(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}