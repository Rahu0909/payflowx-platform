package com.payflowx.user.controller;

import com.payflowx.user.dto.ApiResponse;
import com.payflowx.user.dto.KycResponse;
import com.payflowx.user.dto.SubmitKycRequest;
import com.payflowx.user.service.KycService;
import com.payflowx.user.util.HeaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/kyc")
public class UserKycController {

    private final KycService kycService;

    @PostMapping
    public ApiResponse<KycResponse> submit(
            HttpServletRequest request,
            @Valid @RequestBody SubmitKycRequest body
    ) {
        UUID userId = HeaderUtil.extractUserId(request);
        return ApiResponse.success(kycService.submitKyc(userId, body), "KYC submitted");
    }

    @GetMapping
    public ApiResponse<KycResponse> get(HttpServletRequest request) {
        UUID userId = HeaderUtil.extractUserId(request);
        return ApiResponse.success(kycService.getKyc(userId));
    }

    @PutMapping
    public ApiResponse<KycResponse> update(
            HttpServletRequest request,
            @Valid @RequestBody SubmitKycRequest body
    ) {
        UUID userId = HeaderUtil.extractUserId(request);
        return ApiResponse.success(kycService.updateKyc(userId, body), "KYC updated");
    }
}