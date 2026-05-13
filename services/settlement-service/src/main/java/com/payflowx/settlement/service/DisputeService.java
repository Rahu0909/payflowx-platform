package com.payflowx.settlement.service;

import com.payflowx.settlement.dto.response.DisputeResponse;
import com.payflowx.settlement.enums.DisputeReason;

import java.math.BigDecimal;
import java.util.UUID;

public interface DisputeService {

    DisputeResponse createDispute(UUID paymentId, UUID merchantId, BigDecimal amount, DisputeReason reason, String description);

    DisputeResponse resolveDispute(UUID disputeId, boolean merchantWon);
}