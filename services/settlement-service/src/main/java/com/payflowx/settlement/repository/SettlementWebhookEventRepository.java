package com.payflowx.settlement.repository;

import com.payflowx.settlement.entity.SettlementWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SettlementWebhookEventRepository extends JpaRepository<SettlementWebhookEvent, UUID> {

    List<SettlementWebhookEvent> findTop100ByProcessedFalseOrderByCreatedAtAsc();
}