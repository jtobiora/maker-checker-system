package com.swiftfingers.makercheckersystem.model.permissions;


import java.util.Arrays;

public enum PermissionType {

    ADMIN("Admin"),
    USER("User"),
    READ("Read"),
    ALL("All");

    PermissionType(String description) {
        this.description = description;
    }

    private String description;

    private String name;

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public static PermissionType fromValue(String val) {
        return Arrays.stream(PermissionType.values()).filter(l -> l.getDescription().equalsIgnoreCase(val))
                .findFirst().orElse(null);
    }
}