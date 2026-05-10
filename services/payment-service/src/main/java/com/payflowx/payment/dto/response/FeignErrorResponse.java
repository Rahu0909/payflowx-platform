package com.payflowx.payment.dto.response;

import java.time.LocalDateTime;

public record FeignErrorResponse(

        String status,

        String data,

        String message,

        LocalDateTime timestamp) {
}