package com.payflowx.auth.dto;

public record LoginResponse(
        String fullName,
        String email,
        String role,
        String token,
        String tokenType,
        long expiresIn
) {
}
