package com.payflowx.payment.controller;

import com.payflowx.payment.dto.ApiResponse;
import com.payflowx.payment.dto.request.CreatePaymentRequest;
import com.payflowx.payment.dto.response.PaymentResponse;
import com.payflowx.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ApiResponse<PaymentResponse> createPayment(@RequestHeader(value = "Idempotency-Key", required = false)
                                                          String idempotencyKey,
                                                      @Valid @RequestBody CreatePaymentRequest request) {
        return ApiResponse.success(paymentService.createPayment(request, idempotencyKey), "Payment processed successfully");
    }

    @GetMapping("/{paymentReference}")
    public ApiResponse<PaymentResponse> getPayment(@PathVariable String paymentReference) {
        return ApiResponse.success(paymentService.getPayment(paymentReference), "Payment fetched successfully");
    }
}