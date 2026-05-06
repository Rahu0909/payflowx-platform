package com.payflowx.merchant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateMerchantRequest(

        @NotBlank
        String businessName,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String phone,

        @NotBlank
        String category
) {}