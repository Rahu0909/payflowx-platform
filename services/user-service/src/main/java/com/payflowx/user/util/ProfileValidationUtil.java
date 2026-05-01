package com.payflowx.user.util;

import com.payflowx.user.constant.ValidationMessages;
import com.payflowx.user.exception.BusinessValidationException;

import java.net.URI;
import java.time.LocalDate;
import java.time.Period;

public final class ProfileValidationUtil {

    private ProfileValidationUtil() {}

    public static void validateDob(LocalDate dob) {
        if (dob == null) return;

        if (dob.isAfter(LocalDate.now())) {
            throw new BusinessValidationException(ValidationMessages.INVALID_DOB);
        }

        if (dob.isBefore(LocalDate.of(1900, 1, 1))) {
            throw new BusinessValidationException(ValidationMessages.INVALID_DOB);
        }

        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 18) {
            throw new BusinessValidationException(ValidationMessages.UNDERAGE);
        }
    }

    public static void validateAvatarUrl(String url) {
        if (url == null) return;

        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equals("http") || scheme.equals("https"))) {
                throw new BusinessValidationException(ValidationMessages.INVALID_AVATAR_URL);
            }
        } catch (Exception ex) {
            throw new BusinessValidationException(ValidationMessages.INVALID_AVATAR_URL);
        }
    }
}