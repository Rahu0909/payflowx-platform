package com.payflowx.audit.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        String status,
        String message,
        List<String> errors,
        LocalDateTime timestamp
) {
}