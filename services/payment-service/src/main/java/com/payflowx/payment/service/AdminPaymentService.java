package com.payflowx.payment.service;

import com.payflowx.payment.dto.response.PaymentAttemptResponse;
import com.payflowx.payment.dto.response.PaymentResponse;
import com.payflowx.payment.dto.response.RefundResponse;
import com.payflowx.payment.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPaymentService {

    Page<PaymentResponse> getPayments(PaymentStatus status, Pageable pageable);

    PaymentResponse getPayment(String paymentReference);

    Page<PaymentAttemptResponse> getAttempts(String paymentReference, Pageable pageable);

    Page<RefundResponse> getRefunds(String paymentReference, Pageable pageable);
}