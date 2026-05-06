package com.payflowx.merchant.dto.response;

public record MerchantKycResponse(
        String businessRegistrationNumber,
        String taxId,
        String businessPan,
        String documentUrl,
        String kycStatus,
        String rejectionReason
) {}