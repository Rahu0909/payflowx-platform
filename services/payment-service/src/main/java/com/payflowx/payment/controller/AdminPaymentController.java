package com.payflowx.payment.controller;

import com.payflowx.payment.dto.ApiResponse;
import com.payflowx.payment.dto.response.PaymentAttemptResponse;
import com.payflowx.payment.dto.response.PaymentResponse;
import com.payflowx.payment.dto.response.RefundResponse;
import com.payflowx.payment.enums.PaymentStatus;
import com.payflowx.payment.service.AdminPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final AdminPaymentService adminPaymentService;

    @GetMapping
    public ApiResponse<Page<PaymentResponse>> getPayments(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(adminPaymentService.getPayments(status, PageRequest.of(page, size))
                , "Payments fetched successfully");
    }

    @GetMapping("/{paymentReference}")
    public ApiResponse<PaymentResponse> getPayment(
            @PathVariable String paymentReference) {
        return ApiResponse.success(adminPaymentService.getPayment(paymentReference), "Payment fetched successfully");
    }

    @GetMapping("/{paymentReference}/attempts")
    public ApiResponse<Page<PaymentAttemptResponse>> getAttempts(
            @PathVariable String paymentReference,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(adminPaymentService.getAttempts(paymentReference, PageRequest.of(page, size))
                , "Payment attempts fetched successfully");
    }

    @GetMapping("/{paymentReference}/refunds")
    public ApiResponse<Page<RefundResponse>> getRefunds(
            @PathVariable String paymentReference,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(adminPaymentService.getRefunds(paymentReference, PageRequest.of(page, size))
                , "Refunds fetched successfully");
    }
}