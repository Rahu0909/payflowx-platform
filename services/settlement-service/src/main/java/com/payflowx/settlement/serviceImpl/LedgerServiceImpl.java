package com.payflowx.settlement.serviceImpl;

import com.payflowx.settlement.entity.LedgerEntry;
import com.payflowx.settlement.enums.LedgerAccountType;
import com.payflowx.settlement.enums.LedgerEntryType;
import com.payflowx.settlement.repository.LedgerEntryRepository;
import com.payflowx.settlement.service.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;

    @Override
    @Transactional
    public void recordSettlementEntry(UUID merchantId, UUID settlementId, BigDecimal amount, String currency) {
        String transactionRef = "txn_settlement_" + settlementId;
        /*
         * PLATFORM CASH DEBIT
         */
        saveEntry(transactionRef, merchantId, LedgerAccountType.PLATFORM_CASH,
                LedgerEntryType.DEBIT, amount, currency, settlementId, "Settlement debit");
        /*
         * MERCHANT PAYABLE CREDIT
         */
        saveEntry(transactionRef, merchantId, LedgerAccountType.MERCHANT_PAYABLE,
                LedgerEntryType.CREDIT, amount, currency, settlementId, "Settlement payable");
        log.info("Settlement ledger recorded settlementId={}", settlementId);
    }

    @Override
    @Transactional
    public void recordPayoutEntry(UUID merchantId, UUID payoutId, BigDecimal amount, String currency) {
        String transactionRef = "txn_payout_" + payoutId;
        /*
         * MERCHANT PAYABLE DEBIT
         */
        saveEntry(transactionRef, merchantId, LedgerAccountType.MERCHANT_PAYABLE,
                LedgerEntryType.DEBIT, amount, currency, payoutId, "Payout debit");
        /*
         * PLATFORM CASH CREDIT
         */
        saveEntry(transactionRef, merchantId, LedgerAccountType.PLATFORM_CASH,
                LedgerEntryType.CREDIT, amount, currency, payoutId, "Payout cash credit");
        log.info("Payout ledger recorded payoutId={}", payoutId);
    }

    @Override
    @Transactional
    public void recordReserveEntry(UUID merchantId, UUID disputeId, BigDecimal amount, String currency) {
        String transactionRef = "txn_reserve_" + disputeId;
        /*
         * MERCHANT PAYABLE DEBIT
         */
        saveEntry(transactionRef, merchantId, LedgerAccountType.MERCHANT_PAYABLE, LedgerEntryType.DEBIT, amount, currency, disputeId, "Reserve debit");
        /*
         * MERCHANT RESERVED CREDIT
         */
        saveEntry(transactionRef, merchantId, LedgerAccountType.MERCHANT_RESERVED, LedgerEntryType.CREDIT, amount, currency, disputeId, "Reserve hold");
        log.info("Reserve ledger recorded disputeId={}", disputeId);
    }

    private void saveEntry(String transactionReference, UUID merchantId, LedgerAccountType accountType, LedgerEntryType entryType, BigDecimal amount, String currency, UUID referenceId, String description) {
        ledgerEntryRepository.save(LedgerEntry.builder().transactionReference(transactionReference)
                .merchantId(merchantId).accountType(accountType).entryType(entryType)
                .amount(amount).currency(currency).referenceId(referenceId)
                .description(description).build());
    }
}