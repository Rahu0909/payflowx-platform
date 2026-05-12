package com.payflowx.settlement.service;

import com.payflowx.settlement.dto.request.CreatePayoutRequest;
import com.payflowx.settlement.dto.response.PayoutResponse;

import java.util.UUID;

public interface PayoutService {

    PayoutResponse createPayout(UUID merchantId, CreatePayoutRequest request);
}