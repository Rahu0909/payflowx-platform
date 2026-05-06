package com.payflowx.merchant.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyResponse(
        UUID id,
        String publicKey,
        String secretKey,
        String keyPrefix,
        String environment,
        String status,
        LocalDateTime createdAt,
        LocalDateTime lastUsedAt
) {}