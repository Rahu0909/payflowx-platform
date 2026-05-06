package com.payflowx.merchant.service;

import com.payflowx.merchant.dto.request.CreateMerchantRequest;
import com.payflowx.merchant.dto.request.UpdateMerchantStatusRequest;
import com.payflowx.merchant.dto.response.AdminMerchantResponse;
import com.payflowx.merchant.dto.response.MerchantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MerchantService {

    MerchantResponse createMerchant(UUID authUserId, CreateMerchantRequest request);

    MerchantResponse getMerchantByUser(UUID authUserId);

    Page<AdminMerchantResponse> getAllMerchants(Pageable pageable);

    AdminMerchantResponse getMerchantById(UUID merchantId);

    AdminMerchantResponse updateMerchantStatus(UUID merchantId, UpdateMerchantStatusRequest request);

    void deactivateMerchant(UUID merchantId);
}