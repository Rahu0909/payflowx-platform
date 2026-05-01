package com.payflowx.user.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        String phone,
        String avatarUrl,
        LocalDate dateOfBirth,
        String nationality,
        String accountStatus,
        boolean isVerified,
        List<AddressResponse> addresses,
        boolean onboardingCompleted
) {}