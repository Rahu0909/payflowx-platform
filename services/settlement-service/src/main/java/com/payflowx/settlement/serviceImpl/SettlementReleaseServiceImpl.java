package com.payflowx.settlement.serviceImpl;

import com.payflowx.settlement.entity.Settlement;
import com.payflowx.settlement.enums.SettlementStatus;
import com.payflowx.settlement.repository.SettlementRepository;
import com.payflowx.settlement.service.MerchantBalanceService;
import com.payflowx.settlement.service.SettlementReleaseService;
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

    @Override
    @Transactional
    public void releaseSettlements() {
        List<Settlement> settlements = settlementRepository.findByStatusAndReleaseAtBefore(
                SettlementStatus.PENDING, LocalDateTime.now(), PageRequest.of(0, 100));
        if (settlements.isEmpty()) {
            log.debug("No settlements eligible for release");
            return;
        }

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
            log.info("Settlement released settlementId={} merchantId={} amount={}", settlement.getId(), settlement.getMerchantId(), settlement.getAmount());
        });
        settlementRepository.saveAll(settlements);
        log.info("Settlement release completed count={}", settlements.size());
    }
}