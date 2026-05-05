package com.payflowx.user.dto;

import java.util.UUID;
import com.payflowx.user.entity.enums.AccountStatus;

public record AdminUserResponse(
        UUID id,
        String email,
        AccountStatus accountStatus,
        boolean verified,
        boolean onboardingCompleted
) {}