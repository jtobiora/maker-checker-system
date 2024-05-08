package com.swiftfingers.makercheckersystem.enums;

public enum Errors {
    EXPIRED_SESSION("Your Session has expired."),
    EXPIRED_TOKEN("The token has expired."),
    UNKNOWN_USER("The user is not known."),
    UNAUTHORIZED("User has not been authorized!."),
    NOT_PERMITTED("Permission not granted.");

    private String value;

    private Errors(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}