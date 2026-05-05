package com.payflowx.user.dto;

public record EligibilityResponse(
        boolean eligible,
        String reason
) {}