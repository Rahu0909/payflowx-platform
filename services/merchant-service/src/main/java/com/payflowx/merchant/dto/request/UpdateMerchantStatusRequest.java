package com.payflowx.merchant.dto.request;

import com.payflowx.merchant.enums.MerchantStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateMerchantStatusRequest(

        @NotNull
        MerchantStatus status,

        String reason
) {}