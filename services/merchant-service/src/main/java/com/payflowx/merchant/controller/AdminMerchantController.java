package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.request.UpdateMerchantStatusRequest;
import com.payflowx.merchant.dto.response.AdminMerchantResponse;
import com.payflowx.merchant.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/merchants")
@RequiredArgsConstructor
public class AdminMerchantController {

    private final MerchantService merchantService;

    @GetMapping
    public ApiResponse<Page<AdminMerchantResponse>> getAllMerchants(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(merchantService.getAllMerchants(PageRequest.of(page, size)), "Merchants fetched successfully");
    }

    @GetMapping("/{merchantId}")
    public ApiResponse<AdminMerchantResponse> getMerchant(@PathVariable UUID merchantId) {
        return ApiResponse.success(merchantService.getMerchantById(merchantId), "Merchant fetched successfully");
    }

    @PatchMapping("/{merchantId}/status")
    public ApiResponse<AdminMerchantResponse> updateStatus(@PathVariable UUID merchantId, @Valid @RequestBody UpdateMerchantStatusRequest request) {
        return ApiResponse.success(merchantService.updateMerchantStatus(merchantId, request), "Merchant status updated successfully");
    }

    @DeleteMapping("/{merchantId}")
    public ApiResponse<Void> deactivateMerchant(@PathVariable UUID merchantId) {
        merchantService.deactivateMerchant(merchantId);
        return ApiResponse.success(null, "Merchant deactivated successfully");
    }
}