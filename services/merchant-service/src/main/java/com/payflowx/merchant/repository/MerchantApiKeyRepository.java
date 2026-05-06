package com.payflowx.merchant.repository;

import com.payflowx.merchant.entity.MerchantApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantApiKeyRepository
        extends JpaRepository<MerchantApiKey, UUID> {

    List<MerchantApiKey> findByMerchantIdAndDeletedFalse(UUID merchantId);

    Optional<MerchantApiKey> findByPublicKeyAndDeletedFalse(String publicKey);

    long countByMerchantIdAndDeletedFalse(UUID merchantId);
}