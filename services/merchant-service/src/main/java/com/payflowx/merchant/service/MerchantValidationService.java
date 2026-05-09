package com.payflowx.merchant.service;

import com.payflowx.merchant.dto.response.InternalMerchantValidationResponse;

import java.util.UUID;

public interface MerchantValidationService {

    InternalMerchantValidationResponse validateMerchant(UUID userId);
}