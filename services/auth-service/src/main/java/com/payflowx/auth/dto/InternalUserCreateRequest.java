package com.payflowx.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InternalUserCreateRequest(

        @NotNull(message = "Auth user ID is required")
        UUID authUserId,

        @NotNull(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {}