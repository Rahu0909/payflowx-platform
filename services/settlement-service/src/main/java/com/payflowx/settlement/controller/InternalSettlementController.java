package com.payflowx.settlement.controller;

import com.payflowx.settlement.dto.ApiResponse;
import com.payflowx.settlement.dto.response.SettlementResponse;
import com.payflowx.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/settlements")
@RequiredArgsConstructor
public class InternalSettlementController {
    private final SettlementService settlementService;

    @PostMapping("/payments/{paymentId}")
    public ApiResponse<SettlementResponse> createSettlement(
            @PathVariable UUID paymentId) {
        return ApiResponse.success(settlementService.createSettlement(paymentId), "Settlement created successfully");
    }
}