package com.payflowx.settlement.controller;

import com.payflowx.settlement.dto.ApiResponse;
import com.payflowx.settlement.dto.request.CreatePayoutRequest;
import com.payflowx.settlement.dto.response.PayoutResponse;
import com.payflowx.settlement.service.PayoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/settlements/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;

    @PostMapping("/merchants/{merchantId}")
    public ApiResponse<PayoutResponse> createPayout(
            @PathVariable UUID merchantId,
            @Valid @RequestBody CreatePayoutRequest request) {
        return ApiResponse.success(payoutService.createPayout(merchantId, request), "Payout created successfully");
    }
}