package com.payflowx.settlement.repository;

import com.payflowx.settlement.entity.Settlement;
import com.payflowx.settlement.enums.SettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {

    Page<Settlement> findByMerchantId(UUID merchantId, Pageable pageable);

    Page<Settlement> findByStatus(SettlementStatus status, Pageable pageable);
}