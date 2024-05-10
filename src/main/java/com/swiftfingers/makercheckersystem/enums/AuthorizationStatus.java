package com.swiftfingers.makercheckersystem.enums;

public enum AuthorizationStatus {
    AUTHORIZED("Authorized"),
    INITIALIZED_TOGGLE("Toggle Initialized"),
    INITIALIZED_CREATE("Create Initialized"),
    INITIALIZED_UPDATE("Update Initialized"),
    CREATION_REJECTED("Creation Rejected"),
    UPDATE_REJECTED("Update Rejected"),
    TOGGLE_REJECTED("Toggle Rejected");

    private String value;

    AuthorizationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return  value;
    }

}
