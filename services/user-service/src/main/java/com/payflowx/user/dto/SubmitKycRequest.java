package com.payflowx.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SubmitKycRequest(

        @NotBlank(message = "Legal name is required")
        @Size(max = 150)
        String legalName,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotBlank(message = "Nationality is required")
        String nationality,

        @NotBlank(message = "Government ID is required")
        String governmentId
) {}