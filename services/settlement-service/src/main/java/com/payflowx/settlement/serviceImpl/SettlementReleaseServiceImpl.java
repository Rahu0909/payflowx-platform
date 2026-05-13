package com.payflowx.settlement.serviceImpl;

import com.payflowx.settlement.entity.Settlement;
import com.payflowx.settlement.enums.SettlementStatus;
import com.payflowx.settlement.enums.SettlementWebhookEventType;
import com.payflowx.settlement.repository.SettlementRepository;
import com.payflowx.settlement.service.MerchantBalanceService;
import com.payflowx.settlement.service.SettlementReleaseService;
import com.payflowx.settlement.service.SettlementWebhookEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementReleaseServiceImpl implements SettlementReleaseService {
    private final SettlementRepository settlementRepository;
    private final MerchantBalanceService merchantBalanceService;
    private final SettlementWebhookEventService webhookEventService;

    @Override
    @Transactional
    public void releaseSettlements() {
        log.info("Checking settlements before={}", LocalDateTime.now());
        List<Settlement> settlements = settlementRepository.findEligibleSettlements(SettlementStatus.PENDING, LocalDateTime.now(), PageRequest.of(0, 100));
        if (settlements.isEmpty()) {
            log.debug("No settlements eligible for release");
            return;
        }
        log.info("Eligible settlements count={}", settlements.size());
        settlements.forEach(settlement -> {
            /*
             * MOVE BALANCE
             */
            merchantBalanceService.movePendingToAvailable(settlement.getMerchantId(), settlement.getAmount());
            /*
             * UPDATE STATUS
             */
            settlement.setStatus(SettlementStatus.COMPLETED);
            settlement.setCompletedAt(LocalDateTime.now());
            webhookEventService.publishSettlementEvent(settlement, SettlementWebhookEventType.SETTLEMENT_COMPLETED);
            log.info("Settlement released settlementId={} merchantId={} amount={}", settlement.getId(), settlement.getMerchantId(), settlement.getAmount());
        });
        settlementRepository.saveAll(settlements);
        log.info("Settlement release completed count={}", settlements.size());
    }
}