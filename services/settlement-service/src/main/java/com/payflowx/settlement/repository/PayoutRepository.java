package com.payflowx.settlement.repository;

import com.payflowx.settlement.entity.Payout;
import com.payflowx.settlement.enums.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PayoutRepository extends JpaRepository<Payout, UUID> {

    List<Payout> findTop100ByStatusInAndNextRetryAtBefore(List<PayoutStatus> statuses, LocalDateTime retryAt);
}