package com.payflowx.settlement.repository;

import com.payflowx.settlement.entity.Settlement;
import com.payflowx.settlement.enums.SettlementStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {


    Optional<Settlement> findByPaymentId(UUID paymentId);

    List<Settlement> findByStatusAndReleaseAtBefore(SettlementStatus status, LocalDateTime releaseAt, Pageable pageable);
}