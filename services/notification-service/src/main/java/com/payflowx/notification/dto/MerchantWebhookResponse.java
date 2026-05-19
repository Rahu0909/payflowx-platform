package com.payflowx.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantWebhookResponse {

    private String merchantId;

    private String webhookUrl;

    private String webhookSecret;

    private Boolean active;
}