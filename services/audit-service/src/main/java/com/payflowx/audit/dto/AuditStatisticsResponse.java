package com.payflowx.audit.dto;

import lombok.Builder;

@Builder
public record AuditStatisticsResponse(

        long totalEvents,

        long paymentEvents,

        long settlementEvents,

        long merchantEvents,

        long authEvents,

        long userEvents,

        long orderEvents,

        long notificationEvents

) {
}