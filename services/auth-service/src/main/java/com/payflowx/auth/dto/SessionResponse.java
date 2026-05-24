package com.payflowx.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionResponse(

        UUID sessionId,

        String deviceName,

        String ipAddress,

        String userAgent,

        LocalDateTime loginAt,

        LocalDateTime lastActiveAt,

        LocalDateTime expiresAt) {
}