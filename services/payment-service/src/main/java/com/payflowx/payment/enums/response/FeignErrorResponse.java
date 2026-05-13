package com.payflowx.payment.enums.response;

import java.time.LocalDateTime;

public record FeignErrorResponse(

        String status,

        String data,

        String message,

        LocalDateTime timestamp) {
}