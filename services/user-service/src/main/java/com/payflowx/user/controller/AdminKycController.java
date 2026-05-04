package com.payflowx.user.controller;

import com.payflowx.user.dto.ApiResponse;
import com.payflowx.user.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminKycController {

    private final KycService kycService;

    @PatchMapping("/{id}/kyc/approve")
    public ApiResponse<Void> approve(@PathVariable UUID id) {
        kycService.approveKyc(id);
        return ApiResponse.success(null, "KYC approved");
    }

    @PatchMapping("/{id}/kyc/reject")
    public ApiResponse<Void> reject(
            @PathVariable UUID id,
            @RequestParam String reason
    ) {
        kycService.rejectKyc(id, reason);
        return ApiResponse.success(null, "KYC rejected");
    }
}