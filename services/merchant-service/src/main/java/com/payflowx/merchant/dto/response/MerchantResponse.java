package com.payflowx.merchant.dto.response;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String businessName,
        String email,
        String phone,
        String category,
        String status
) {}