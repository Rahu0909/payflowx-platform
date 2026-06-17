package com.payflowx.notification.repository;

import com.payflowx.notification.entity.WebhookDelivery;
import com.payflowx.notification.enums.WebhookDeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, UUID> {

    List<WebhookDelivery> findByStatusAndNextRetryAtBefore(WebhookDeliveryStatus status, LocalDateTime time);

    Optional<WebhookDelivery> findTopByEventIdOrderByCreatedAtDesc(String eventId);

    boolean existsByEventIdAndStatus(String eventId, WebhookDeliveryStatus status);

    long countByStatus(WebhookDeliveryStatus status);

    long countByStatusAndCreatedAtAfter(WebhookDeliveryStatus status, LocalDateTime from);
}