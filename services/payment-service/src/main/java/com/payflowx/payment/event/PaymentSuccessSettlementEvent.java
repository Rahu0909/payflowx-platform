package com.payflowx.payment.event;

import java.util.UUID;

public record PaymentSuccessSettlementEvent(UUID paymentId) {
}