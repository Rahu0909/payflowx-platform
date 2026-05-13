package com.payflowx.settlement.serviceImpl;

import com.payflowx.settlement.dto.response.DisputeResponse;
import com.payflowx.settlement.entity.Dispute;
import com.payflowx.settlement.enums.DisputeReason;
import com.payflowx.settlement.enums.DisputeStatus;
import com.payflowx.settlement.repository.DisputeRepository;
import com.payflowx.settlement.service.DisputeService;
import com.payflowx.settlement.service.LedgerService;
import com.payflowx.settlement.service.MerchantBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisputeServiceImpl implements DisputeService {
    private final DisputeRepository disputeRepository;
    private final MerchantBalanceService merchantBalanceService;
    private final LedgerService ledgerService;

    @Override
    @Transactional
    public DisputeResponse createDispute(UUID paymentId, UUID merchantId, BigDecimal amount, DisputeReason reason, String description) {
        merchantBalanceService.reserveBalance(merchantId, amount);
        Dispute dispute = Dispute.builder().paymentId(paymentId).merchantId(merchantId).amount(amount)
                .reason(reason).status(DisputeStatus.OPEN).description(description).build();
        disputeRepository.save(dispute);
        ledgerService.recordReserveEntry(
                dispute.getMerchantId(),
                dispute.getId(),
                dispute.getAmount(),
                "INR"
        );
        log.warn("Dispute created disputeId={} merchantId={} amount={}", dispute.getId(), merchantId, amount);
        return map(dispute);
    }

    @Override
    @Transactional
    public DisputeResponse resolveDispute(UUID disputeId, boolean merchantWon) {
        Dispute dispute = disputeRepository.findById(disputeId).orElseThrow();
        if (merchantWon) {
            merchantBalanceService.releaseReservedBalance(dispute.getMerchantId(), dispute.getAmount());
            dispute.setStatus(DisputeStatus.WON);
        } else {
            merchantBalanceService.deductReservedBalance(dispute.getMerchantId(), dispute.getAmount());
            dispute.setStatus(DisputeStatus.LOST);
        }
        dispute.setResolvedAt(LocalDateTime.now());
        disputeRepository.save(dispute);
        log.info("Dispute resolved disputeId={} merchantWon={}", disputeId, merchantWon);
        return map(dispute);
    }

    private DisputeResponse map(Dispute dispute) {
        return new DisputeResponse(dispute.getId(), dispute.getPaymentId()
                , dispute.getMerchantId(), dispute.getAmount(), dispute.getReason()
                , dispute.getStatus(), dispute.getDescription(), dispute.getResolvedAt()
                , dispute.getCreatedAt());
    }
}