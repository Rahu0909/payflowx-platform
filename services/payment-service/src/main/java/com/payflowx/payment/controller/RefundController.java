package com.payflowx.payment.controller;

import com.payflowx.payment.dto.ApiResponse;
import com.payflowx.payment.dto.request.RefundRequest;
import com.payflowx.payment.dto.response.RefundResponse;
import com.payflowx.payment.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;

    @PostMapping("/{paymentReference}/refunds")
    public ApiResponse<RefundResponse> refundPayment(@PathVariable String paymentReference, @Valid @RequestBody RefundRequest request) {
        return ApiResponse.success(refundService.refundPayment(paymentReference, request), "Refund processed successfully");
    }

    @GetMapping("/{paymentReference}/refunds")
    public ApiResponse<List<RefundResponse>> getRefunds(@PathVariable String paymentReference) {
        return ApiResponse.success(refundService.getRefunds(paymentReference), "Refunds fetched successfully");
    }
}