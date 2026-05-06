package com.payflowx.merchant.dto.request;

import com.payflowx.merchant.enums.ApiKeyEnvironment;
import jakarta.validation.constraints.NotNull;

public record CreateApiKeyRequest(

        @NotNull
        ApiKeyEnvironment environment
) {}