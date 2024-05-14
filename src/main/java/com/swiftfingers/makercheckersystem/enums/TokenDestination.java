package com.swiftfingers.makercheckersystem.enums;

public enum TokenDestination {
    EMAIL("Email"),
    SMS("SMS");


    private String value;

    private TokenDestination(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
