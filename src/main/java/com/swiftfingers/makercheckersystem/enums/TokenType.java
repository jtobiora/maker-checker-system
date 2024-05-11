package com.swiftfingers.makercheckersystem.enums;

public enum TokenType {
    HARD_TOKEN("Hard Token"),
    SOFT_TOKEN("Soft Token");


    private String value;

    private TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
