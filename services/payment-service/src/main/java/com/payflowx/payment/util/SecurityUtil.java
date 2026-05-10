package com.payflowx.payment.util;

import com.payflowx.payment.constant.ErrorCode;
import com.payflowx.payment.exception.BusinessValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

public class SecurityUtil {

    private static final String USER_ID_HEADER = "X-Auth-UserId";
    private static final String ROLE_HEADER = "X-Auth-Role";

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessValidationException(ErrorCode.INVALID_REQUEST);
        }
        return attributes.getRequest();
    }

    // Get User ID (STRICT)
    public static UUID getCurrentUserId() {
        String userId = getRequest().getHeader(USER_ID_HEADER);
        if (userId == null || userId.isBlank()) {
            throw new BusinessValidationException(ErrorCode.INVALID_REQUEST);
        }
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException ex) {
            throw new BusinessValidationException(ErrorCode.INVALID_REQUEST);
        }
    }

    // Get Role (for future use)
    public static String getCurrentUserRole() {
        String role = getRequest().getHeader(ROLE_HEADER);
        if (role == null || role.isBlank()) {
            throw new BusinessValidationException(ErrorCode.INVALID_REQUEST);
        }
        return role;
    }
}