package com.payflowx.user.service;

import com.payflowx.user.dto.EligibilityResponse;

import java.util.UUID;

public interface UserValidationService {

    EligibilityResponse validateUserForPayment(UUID userId);

    void validateOrThrow(UUID userId);
}