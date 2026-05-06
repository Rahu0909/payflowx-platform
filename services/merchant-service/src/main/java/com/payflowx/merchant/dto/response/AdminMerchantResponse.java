package com.payflowx.merchant.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminMerchantResponse(
        UUID id,
        UUID authUserId,
        String businessName,
        String email,
        String phone,
        String category,
        String status,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID statusChangedBy,
        LocalDateTime statusChangedAt,
        String statusReason
) {}