package com.payflowx.auth.dto;

import java.util.UUID;

public record UserResponse(
        UUID authUserId,
        String fullName,
        String email,
        String role
) {
}
