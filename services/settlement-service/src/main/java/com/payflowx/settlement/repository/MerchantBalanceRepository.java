package com.payflowx.settlement.repository;

import com.payflowx.settlement.entity.MerchantBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantBalanceRepository extends JpaRepository<MerchantBalance, UUID> {

    Optional<MerchantBalance> findByMerchantId(UUID merchantId);
}