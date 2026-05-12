package com.payflowx.merchant.service;

import com.payflowx.merchant.dto.request.UpdateMerchantSettlementConfigRequest;
import com.payflowx.merchant.dto.response.InternalMerchantSettlementConfigResponse;
import com.payflowx.merchant.dto.response.MerchantSettlementConfigResponse;

import java.util.UUID;

public interface MerchantSettlementConfigService {

    InternalMerchantSettlementConfigResponse getSettlementConfig(UUID merchantId);

    MerchantSettlementConfigResponse getMerchantSettlementConfig(UUID authUserId);

    MerchantSettlementConfigResponse updateSettlementConfig(UUID merchantId, UpdateMerchantSettlementConfigRequest request);

    MerchantSettlementConfigResponse getAdminSettlementConfig(UUID merchantId);
}