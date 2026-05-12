package com.payflowx.settlement.client;

import com.payflowx.settlement.dto.ApiResponse;
import com.payflowx.settlement.dto.response.InternalPaymentSettlementResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @GetMapping("/internal/payments/{paymentId}/settlement")
    ApiResponse<InternalPaymentSettlementResponse> getPaymentSettlementData(
            @PathVariable UUID paymentId);
}