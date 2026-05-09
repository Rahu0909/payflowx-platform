package com.payflowx.gateway.security;

import com.payflowx.gateway.constants.AppConstants;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class RoleBasedAccessValidator {

    private static final Map<String, Set<String>> ACCESS_RULES = Map.of(
            "/admin", Set.of(AppConstants.ADMIN),
            "/merchants", Set.of(AppConstants.USER, AppConstants.ADMIN),
            "/users", Set.of(AppConstants.USER, AppConstants.ADMIN),
            "/orders", Set.of(AppConstants.USER, AppConstants.ADMIN)
    );

    public boolean hasAccess(String path, String role) {

        for (var entry : ACCESS_RULES.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue().contains(role);
            }
        }

        return true;
    }
}