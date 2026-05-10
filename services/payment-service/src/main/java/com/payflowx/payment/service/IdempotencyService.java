package com.payflowx.payment.service;

import com.payflowx.payment.entity.PaymentIdempotency;

import java.util.UUID;

public interface IdempotencyService {

    PaymentIdempotency validateOrGet(String idempotencyKey, String requestHash);

    void saveRecord(String idempotencyKey, String requestHash, UUID paymentId, String responsePayload);
}