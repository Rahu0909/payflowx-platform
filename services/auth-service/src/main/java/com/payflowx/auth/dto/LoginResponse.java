package com.payflowx.auth.dto;

import java.util.UUID;

public record LoginResponse(
        UUID authUserId,
        String fullName,
        String email,
        String role,
        String token,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}
