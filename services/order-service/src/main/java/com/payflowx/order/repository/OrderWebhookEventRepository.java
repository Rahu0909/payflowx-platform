package com.payflowx.order.repository;

import com.payflowx.order.entity.OrderWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderWebhookEventRepository extends JpaRepository<OrderWebhookEvent, UUID> {

    List<OrderWebhookEvent> findTop100ByProcessedFalseOrderByCreatedAtAsc();
}