package com.payflowx.payment.service;

import com.payflowx.payment.dto.request.CreatePaymentRequest;
import com.payflowx.payment.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request, String idempotencyKey);

    PaymentResponse getPayment(String paymentReference);
}