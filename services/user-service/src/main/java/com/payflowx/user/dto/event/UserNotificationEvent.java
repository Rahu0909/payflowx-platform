package com.payflowx.user.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationEvent {

    private UUID eventId;

    private UUID userId;

    private String email;

    private String fullName;

    private String eventType;

    private String message;

    private LocalDateTime occurredAt;
}