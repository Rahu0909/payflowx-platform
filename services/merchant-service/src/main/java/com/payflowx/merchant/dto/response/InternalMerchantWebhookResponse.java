package com.payflowx.merchant.dto.response;

import lombok.Builder;

@Builder
public record InternalMerchantWebhookResponse(

        String merchantId,

        String webhookUrl,

        String webhookSecret,

        Boolean active) {
}