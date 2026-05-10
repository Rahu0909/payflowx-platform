package com.payflowx.payment.service;

import com.payflowx.payment.dto.response.PaymentGatewayResponse;
import com.payflowx.payment.entity.Payment;
import com.payflowx.payment.entity.PaymentAttempt;

import java.util.UUID;

public interface PaymentAttemptService {

    PaymentAttempt createAttempt(Payment payment);

    void markAttemptSuccess(UUID attemptId, PaymentGatewayResponse response, long processingTimeMs);

    void markAttemptFailed(UUID attemptId, PaymentGatewayResponse response, long processingTimeMs);
}