package com.payflowx.payment.controller;

import com.payflowx.payment.dto.ApiResponse;
import com.payflowx.payment.dto.response.InternalPaymentSettlementResponse;
import com.payflowx.payment.service.InternalPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/payments")
@RequiredArgsConstructor
public class InternalPaymentController {
    private final InternalPaymentService internalPaymentService;

    @GetMapping("/{paymentId}/settlement")
    public ApiResponse<InternalPaymentSettlementResponse> getSettlementPayment(@PathVariable UUID paymentId) {
        return ApiResponse.success(internalPaymentService.getSettlementPayment(paymentId)
                , "Settlement payment fetched successfully");
    }
}