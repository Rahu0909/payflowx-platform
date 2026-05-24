package com.payflowx.auth.constant;

public final class ErrorCode {

    private ErrorCode() {
    }

    public static final String USER_NOT_FOUND = "User not found";

    public static final String INVALID_RESET_TOKEN = "Invalid password reset token";

    public static final String RESET_TOKEN_EXPIRED = "Password reset token expired";
}