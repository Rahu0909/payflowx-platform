package com.payflowx.settlement.service;

import com.payflowx.settlement.dto.response.MerchantBalanceResponse;
import com.payflowx.settlement.entity.MerchantBalance;

import java.math.BigDecimal;
import java.util.UUID;

public interface MerchantBalanceService {

    MerchantBalance getOrCreateBalance(UUID merchantId);

    void addPendingBalance(UUID merchantId, BigDecimal amount);

    void movePendingToAvailable(UUID merchantId, BigDecimal amount);

    void deductAvailableBalance(UUID merchantId, BigDecimal amount);

    MerchantBalanceResponse getBalance(UUID merchantId);
}