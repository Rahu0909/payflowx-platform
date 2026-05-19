package com.payflowx.notification.client;

import com.payflowx.notification.dto.MerchantWebhookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "merchant-service")
public interface MerchantServiceClient {

    @GetMapping("/internal/merchants/{merchantId}/webhook")
    MerchantWebhookResponse getMerchantWebhook(@PathVariable("merchantId") String merchantId);
}