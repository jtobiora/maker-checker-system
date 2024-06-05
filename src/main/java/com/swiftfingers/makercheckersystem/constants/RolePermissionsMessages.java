package com.swiftfingers.makercheckersystem.constants;

public class RolePermissionsMessages {
    public static final String ROLE_EXISTS = "Role '%s' already exists";
    public static final String ROLE_STATE_MANDATORY = "Role state cannot be empty";
    public static final String INVALID_ROLE_STATE = "Invalid Role state provided";

    public static final String DUPLICATE_ROLE_ASSIGNED = "Role cannot be assigned more than once";
    public static final String ERR_USER_ROLE_ASSIGN = "User must be activated and Authorized to be given a role.";

    public static final String ERR_ROLE_INACTIVE = "Role which is not Activated and Authorized cannot be assigned.";

    public static final String USER_ROLE_NOT_FOUND = "No user exists with the role";

    public static final String USER_ROLE_ASSIGNED = "User role has been added and awaiting authorization";
    public static final String USER_ROLE_UNASSIGNED = "User has been removed from '%s' role";

    public static final String ROLE_AUTHORITY_NOT_FOUND = "No role exists with these permissions: %s";

    public static final String PERMISSIONS_REMOVED = "Permissions '%s' have been removed from role";
}
