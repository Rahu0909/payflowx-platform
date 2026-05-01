package com.payflowx.user.constant;

public final class ValidationMessages {

    private ValidationMessages() {}

    public static final String UNDERAGE = "User must be at least 18 years old";
    public static final String INVALID_DOB = "Invalid date of birth";
    public static final String INVALID_AVATAR_URL = "Avatar URL must be a valid http/https URL";
    public static final String PROFILE_LOCKED_KYC = "Profile cannot be modified after KYC is verified";
}
