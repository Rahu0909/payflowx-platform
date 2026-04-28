package com.payflowx.auth.constant;

public final class AppConstants {

    private AppConstants() {
    }

    public static final String API_BASE = "/auth";
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    public static final String ROLE_USER = "USER";

    public static final String USER_REGISTERED = "User registered successfully";
    public static final String LOGIN_SUCCESS = "Login successful";

    public static final String INVALID_CREDENTIALS = "Invalid email or password";

    public static final String BEARER = "Bearer";
    public static final String REFRESH = "/refresh";
    public static final String LOGOUT = "/logout";

    public static final String LOGOUT_SUCCESS = "Logout successful";
    public static final String TOKEN_REFRESHED = "Token refreshed";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
}