package com.payflowx.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ValidateApiKeyRequest(

        @NotBlank
        String secretKey
) {}