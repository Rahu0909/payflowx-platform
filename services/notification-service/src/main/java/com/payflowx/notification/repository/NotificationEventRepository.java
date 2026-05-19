package com.payflowx.notification.repository;

import com.payflowx.notification.entity.NotificationEvent;
import com.payflowx.notification.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, UUID> {

    Optional<NotificationEvent> findByEventId(String eventId);

    List<NotificationEvent> findByStatusAndNextRetryAtBefore(NotificationStatus status, LocalDateTime time);
}