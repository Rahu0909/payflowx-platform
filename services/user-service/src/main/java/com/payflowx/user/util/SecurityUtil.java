package com.payflowx.user.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public class SecurityUtil {
    public static UUID getCurrentUserId() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        HttpServletRequest request = attributes.getRequest();
        String userId = request.getHeader("X-Auth-UserId");
        if (userId == null || userId.isBlank()) {
            return null;
        }
        return UUID.fromString(userId);
    }
}