package com.payflowx.merchant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantWebhookResponse {

    private String merchantId;

    private String webhookUrl;

    private String webhookSecret;

    private Boolean webhookEnabled;
}