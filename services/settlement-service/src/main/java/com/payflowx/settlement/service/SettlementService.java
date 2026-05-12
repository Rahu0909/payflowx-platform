package com.payflowx.settlement.service;

import com.payflowx.settlement.dto.response.SettlementResponse;

import java.util.UUID;

public interface SettlementService {

    SettlementResponse createSettlement(UUID paymentId);
}