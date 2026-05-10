package com.payflowx.payment.service;

import com.payflowx.payment.entity.Payment;
import com.payflowx.payment.enums.PaymentEventType;

public interface PaymentEventService {

    void publishEvent(Payment payment, PaymentEventType eventType);
}