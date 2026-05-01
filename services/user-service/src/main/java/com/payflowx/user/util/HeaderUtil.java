package com.payflowx.user.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public class HeaderUtil {

    private HeaderUtil() {}

    public static UUID extractUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-Auth-UserId");

        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("Missing X-Auth-UserId header");
        }

        return UUID.fromString(userId);
    }
}