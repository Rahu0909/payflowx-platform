package com.payflowx.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserRequest(

        @NotBlank(message = "Full name cannot be empty")
        @Size(max = 100, message = "Full name must be less than 100 characters")
        String fullName,

        @Size(max = 255, message = "Avatar URL too long")
        String avatarUrl,

        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone must be 10 digits"
        )
        String phone,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Size(max = 50, message = "Nationality too long")
        String nationality
) {
}