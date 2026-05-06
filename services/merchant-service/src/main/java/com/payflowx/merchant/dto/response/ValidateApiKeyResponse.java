package com.payflowx.merchant.dto.response;

import java.util.UUID;

public record ValidateApiKeyResponse(
        boolean valid,
        UUID merchantId,
        String message
) {}