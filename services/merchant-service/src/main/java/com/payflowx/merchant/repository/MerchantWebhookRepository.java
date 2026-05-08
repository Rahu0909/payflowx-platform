package com.payflowx.merchant.repository;

import com.payflowx.merchant.entity.MerchantWebhook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantWebhookRepository
        extends JpaRepository<MerchantWebhook, UUID> {

    List<MerchantWebhook>
    findByMerchantIdAndDeletedFalse(UUID merchantId);

    Optional<MerchantWebhook>
    findByIdAndDeletedFalse(UUID webhookId);

    long countByMerchantIdAndDeletedFalse(UUID merchantId);
}