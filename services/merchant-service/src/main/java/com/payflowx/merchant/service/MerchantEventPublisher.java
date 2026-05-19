package com.payflowx.merchant.service;

import java.util.UUID;

public interface MerchantEventPublisher {

    void publishMerchantKycApproved(UUID merchantId, String businessName, String email);

    void publishMerchantKycRejected(UUID merchantId, String businessName, String email, String reason);
}