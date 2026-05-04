package com.payflowx.user.service;

import com.payflowx.user.dto.KycResponse;
import com.payflowx.user.dto.SubmitKycRequest;

import java.util.UUID;

public interface KycService {

    KycResponse submitKyc(UUID authUserId, SubmitKycRequest request);

    KycResponse getKyc(UUID authUserId);

    KycResponse updateKyc(UUID authUserId, SubmitKycRequest request);

    void approveKyc(UUID userId);

    void rejectKyc(UUID userId, String reason);
}