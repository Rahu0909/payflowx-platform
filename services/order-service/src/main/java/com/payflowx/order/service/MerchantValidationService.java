package com.payflowx.order.service;

import com.payflowx.order.dto.response.InternalMerchantValidationResponse;

import java.util.UUID;

public interface MerchantValidationService {

    InternalMerchantValidationResponse validateMerchant(UUID userId);
}