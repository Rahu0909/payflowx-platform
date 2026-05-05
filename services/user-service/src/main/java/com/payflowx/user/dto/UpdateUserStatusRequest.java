package com.payflowx.user.dto;

import com.payflowx.user.entity.enums.AccountStatus;

public record UpdateUserStatusRequest(
        AccountStatus status,
        String reason
) {}