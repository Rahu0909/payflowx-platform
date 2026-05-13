package com.payflowx.settlement.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface LedgerService {

    void recordSettlementEntry(UUID merchantId, UUID settlementId, BigDecimal amount, String currency);

    void recordPayoutEntry(UUID merchantId, UUID payoutId, BigDecimal amount, String currency);

    void recordReserveEntry(UUID merchantId, UUID disputeId, BigDecimal amount, String currency);
}