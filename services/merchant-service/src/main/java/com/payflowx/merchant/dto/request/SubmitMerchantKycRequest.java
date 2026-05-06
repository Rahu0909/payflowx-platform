package com.payflowx.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SubmitMerchantKycRequest(

        @NotBlank
        String businessRegistrationNumber,

        @NotBlank
        String taxId,

        @NotBlank
        String businessPan,

        @NotBlank
        String documentUrl
) {}