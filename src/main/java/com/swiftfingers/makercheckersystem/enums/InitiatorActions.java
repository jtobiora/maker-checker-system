package com.swiftfingers.makercheckersystem.enums;

public enum InitiatorActions {

    TOGGLE("Toggle"),
    CREATE("Create "),
    UPDATE("Update");

    private String value;

    InitiatorActions(String value) {
        this.value = value;
    }

    public String getValue() {
        return  value;
    }
}