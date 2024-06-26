package com.swiftfingers.makercheckersystem.constants;

import java.util.List;

public class AppConstants {
    public static final String TOKEN_HEADER = "Authorization";
    public static final int MAX_SESSION_IDLE_TIME_SECONDS = 900;
    public static final String CREATE = "CREATE-ACTION";
    public static final String DELETE = "DELETE-ACTION";

    public static final String UPDATE = "UPDATE-ACTION";
    public static final String TOGGLE = "TOGGLE-ACTION";

    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    public static final List<String> ENTITY_TYPES = List.of("user","role","account","payments");

    public static final List<String> AUTHORIZATION_STATES = List.of("AUTHORIZED","UNAUTHORIZED","REJECTED");

    public static final String REQUEST_INVALID = "Request is invalid. Please provide the entity type and Authorization status";

    public static final String FAILED_TO_LOAD_DATA = "No updated data to load";

    public static final String RESOURCE_NOT_FOUND = "No available resource was found for '%s' entity";

    public static final String INVALID_AUTHORIZATION_STATUS = "Invalid authorization status";
    public static final String ENTITY_TYPES_AVAILABLE = "Entity type must be one of " + ENTITY_TYPES.stream().toList();

    public static final String AUTHORIZATION_STATES_AVAILABLE = "Authorization state must be one of " + AUTHORIZATION_STATES.stream().toList();

    public static final String APPROVAL_ERR_MSG = "Entity Authorization status does not permit Approval/Rejection";
}
