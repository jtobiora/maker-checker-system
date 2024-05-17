package com.swiftfingers.makercheckersystem.constants;

import java.util.List;

public class AppConstants {
    public static final String TOKEN_HEADER = "Authorization";
    public static final int MAX_SESSION_IDLE_TIME_SECONDS = 900;
    public static final String CREATE_ENTITY = "CREATE_ACTION";
    public static final String DELETE_ENTITY = "DELETE_ACTION";

    public static final String UPDATE_ENTITY = "UPDATE_ACTION";

    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    public static final List<String> ENTITY_TYPES = List.of("user","role","account","payments");

    public static final List<String> AUTHORIZATION_STATES = List.of("AUTHORIZED","UNAUTHORIZED","REJECTED");

    public static final String REQUEST_INVALID = "Request is invalid. Please provide the entity type and Authorization status";

    public static final String ENTITY_TYPES_AVAILABLE = "Entity type must be one of " + ENTITY_TYPES.stream().toList();

    public static final String AUTHORIZATION_STATES_AVAILABLE = "Authorization state must be one of " + AUTHORIZATION_STATES.stream().toList();

    public static final String APPROVAL_ERR_MSG = "Entity Authorization status does not permit Approval";
}
