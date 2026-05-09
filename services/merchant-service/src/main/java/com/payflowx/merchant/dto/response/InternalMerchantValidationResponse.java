package com.payflowx.merchant.dto.response;

public record InternalMerchantValidationResponse(
        boolean valid,
        String merchantId,
        String businessName,
        String status,
        boolean kycVerified
) {
}