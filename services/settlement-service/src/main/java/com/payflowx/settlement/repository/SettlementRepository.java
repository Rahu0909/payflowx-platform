package com.payflowx.settlement.repository;

import com.payflowx.settlement.entity.Settlement;
import com.payflowx.settlement.enums.SettlementStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {


    Optional<Settlement> findByPaymentId(UUID paymentId);

    @Query("""
       SELECT s
       FROM Settlement s
       WHERE s.status = :status
       AND s.releaseAt <= :releaseAt
       """)
    List<Settlement> findEligibleSettlements(
            @Param("status") SettlementStatus status,
            @Param("releaseAt") LocalDateTime releaseAt,
            Pageable pageable
    );
}