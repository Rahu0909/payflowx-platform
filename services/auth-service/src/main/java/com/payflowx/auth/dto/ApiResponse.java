package com.payflowx.auth.dto;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        String status,
        String message,
        T data,
        LocalDateTime timestamp
) {
}
