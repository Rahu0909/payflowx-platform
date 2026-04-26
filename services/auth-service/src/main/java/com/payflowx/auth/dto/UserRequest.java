package com.payflowx.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @Email(message = "Invalid email")
        @NotBlank(message = "Email is required")
        String email,

        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {
}
