package com.payflowx.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RejectMerchantKycRequest(
        @NotBlank
        String reason
) {}