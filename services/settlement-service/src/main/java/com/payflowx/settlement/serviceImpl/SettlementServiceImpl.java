package com.payflowx.settlement.serviceImpl;

import com.payflowx.settlement.client.PaymentClient;
import com.payflowx.settlement.constant.ErrorCode;
import com.payflowx.settlement.dto.ApiResponse;
import com.payflowx.settlement.dto.response.InternalPaymentSettlementResponse;
import com.payflowx.settlement.dto.response.SettlementResponse;
import com.payflowx.settlement.entity.Settlement;
import com.payflowx.settlement.enums.SettlementStatus;
import com.payflowx.settlement.enums.SettlementType;
import com.payflowx.settlement.exception.BusinessValidationException;
import com.payflowx.settlement.repository.SettlementRepository;
import com.payflowx.settlement.service.MerchantBalanceService;
import com.payflowx.settlement.service.SettlementService;
import com.payflowx.settlement.util.SettlementReferenceGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {
    private final SettlementRepository settlementRepository;
    private final PaymentClient paymentClient;
    private final MerchantBalanceService merchantBalanceService;

    @Override
    @Transactional
    public SettlementResponse createSettlement(UUID paymentId) {
        settlementRepository.findByPaymentId(paymentId).ifPresent(existing -> {
            throw new BusinessValidationException(ErrorCode.DUPLICATE_SETTLEMENT);
        });
        ApiResponse<InternalPaymentSettlementResponse> response = paymentClient.getPaymentSettlementData(paymentId);
        if (response == null || response.data() == null) {

            throw new BusinessValidationException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        InternalPaymentSettlementResponse payment = response.data();
        Settlement settlement = Settlement.builder().settlementReference(SettlementReferenceGeneratorUtil.generateReference())
                .merchantId(payment.merchantId()).paymentId(payment.paymentId())
                .type(SettlementType.PAYMENT).status(SettlementStatus.PENDING)
                .amount(payment.netSettlementAmount()).currency(payment.currency())
                .description("Settlement for payment " + payment.paymentId())
                .releaseAt(settlementReleaseTime(payment.settlementDelayDays())).build();
        settlementRepository.save(settlement);
        /*
         * ADD TO PENDING BALANCE
         */
        merchantBalanceService.addPendingBalance(payment.merchantId(), payment.netSettlementAmount());
        log.info("Settlement created settlementId={} paymentId={}", settlement.getId(), paymentId);
        return map(settlement);
    }

    private SettlementResponse map(Settlement settlement) {
        return new SettlementResponse(settlement.getId(), settlement.getSettlementReference(), settlement.getMerchantId(), settlement.getPaymentId(), settlement.getType(), settlement.getStatus(), settlement.getAmount(), settlement.getCurrency(), settlement.getDescription(), settlement.getCreatedAt());
    }

    private LocalDateTime settlementReleaseTime(Integer delayDays) {
        return LocalDateTime.now().plusDays(delayDays);
    }
}
