package com.payflowx.merchant.repository;

import com.payflowx.merchant.entity.MerchantSettlementConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantSettlementConfigRepository extends JpaRepository<MerchantSettlementConfig, UUID> {

    Optional<MerchantSettlementConfig> findByMerchantId(UUID merchantId);
}