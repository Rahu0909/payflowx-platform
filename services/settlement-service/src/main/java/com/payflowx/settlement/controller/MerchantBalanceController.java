package com.payflowx.settlement.controller;

import com.payflowx.settlement.dto.ApiResponse;
import com.payflowx.settlement.dto.response.MerchantBalanceResponse;
import com.payflowx.settlement.service.MerchantBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/settlements")
@RequiredArgsConstructor
public class MerchantBalanceController {

    private final MerchantBalanceService merchantBalanceService;

    @GetMapping("/merchants/{merchantId}/balance")
    public ApiResponse<MerchantBalanceResponse> getBalance(@PathVariable UUID merchantId) {
        return ApiResponse.success(merchantBalanceService.getBalance(merchantId), "Merchant balance fetched successfully");
    }
}