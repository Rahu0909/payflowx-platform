package com.payflowx.merchant.service;

import com.payflowx.merchant.dto.request.RejectMerchantKycRequest;
import com.payflowx.merchant.dto.request.SubmitMerchantKycRequest;
import com.payflowx.merchant.dto.response.MerchantKycResponse;

import java.util.UUID;

public interface MerchantKycService {

    MerchantKycResponse submitKyc(UUID authUserId, SubmitMerchantKycRequest request);

    MerchantKycResponse getKyc(UUID authUserId);

    MerchantKycResponse approveKyc(UUID merchantId);

    MerchantKycResponse rejectKyc(UUID merchantId, RejectMerchantKycRequest request);
}