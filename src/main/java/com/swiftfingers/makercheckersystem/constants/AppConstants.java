package com.swiftfingers.makercheckersystem.constants;

public class AppConstants {

    public static final String TOKEN_HEADER = "Authorization";
    public static final int MAX_SESSION_IDLE_TIME_SECONDS = 300;
    public static final String CREATE_ENTITY = "CREATE_ACTION";
    public static final String DELETE_ENTITY = "DELETE_ACTION";

    public static final String UPDATE_ENTITY = "UPDATE_ACTION";

    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
}
