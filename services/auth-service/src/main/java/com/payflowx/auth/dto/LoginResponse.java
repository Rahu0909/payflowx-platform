package com.payflowx.auth.dto;

public record LoginResponse(
        Long id,
        String fullName,
        String email,
        String role,
        String token,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}
