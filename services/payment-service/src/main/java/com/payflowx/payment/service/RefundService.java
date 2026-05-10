package com.payflowx.payment.service;

import com.payflowx.payment.dto.request.RefundRequest;
import com.payflowx.payment.dto.response.RefundResponse;

import java.util.List;

public interface RefundService {

    RefundResponse refundPayment(String paymentReference, RefundRequest request);

    List<RefundResponse> getRefunds(String paymentReference);
}