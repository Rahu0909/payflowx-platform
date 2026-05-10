package com.payflowx.payment.processor;

import com.payflowx.payment.dto.response.PaymentGatewayResponse;
import com.payflowx.payment.entity.Payment;
import com.payflowx.payment.entity.PaymentAttempt;
import com.payflowx.payment.enums.PaymentMethod;

public interface PaymentProcessor {

    PaymentGatewayResponse process(Payment payment, PaymentAttempt attempt);

    PaymentMethod supportedMethod();
}