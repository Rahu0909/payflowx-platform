package com.payflowx.settlement.serviceImpl;

import com.payflowx.settlement.constant.ErrorCode;
import com.payflowx.settlement.dto.request.CreatePayoutRequest;
import com.payflowx.settlement.dto.response.PayoutResponse;
import com.payflowx.settlement.entity.MerchantBalance;
import com.payflowx.settlement.entity.Payout;
import com.payflowx.settlement.enums.PayoutStatus;
import com.payflowx.settlement.enums.SettlementWebhookEventType;
import com.payflowx.settlement.exception.BusinessValidationException;
import com.payflowx.settlement.repository.PayoutRepository;
import com.payflowx.settlement.service.MerchantBalanceService;
import com.payflowx.settlement.service.PayoutService;
import com.payflowx.settlement.service.SettlementWebhookEventService;
import com.payflowx.settlement.util.PayoutReferenceGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutServiceImpl implements PayoutService {
    private final PayoutRepository payoutRepository;
    private final MerchantBalanceService merchantBalanceService;
    private final SettlementWebhookEventService webhookEventService;

    @Override
    @Transactional
    public PayoutResponse createPayout(UUID merchantId, CreatePayoutRequest request) {
        /*
         * VALIDATE AVAILABLE BALANCE
         */
        MerchantBalance balance = merchantBalanceService.getOrCreateBalance(merchantId);
        if (balance.getAvailableBalance().compareTo(request.amount()) < 0) {
            throw new BusinessValidationException(ErrorCode.INSUFFICIENT_AVAILABLE_BALANCE);
        }
        /*
         * CREATE PAYOUT REQUEST
         */
        Payout payout = Payout.builder().payoutReference(PayoutReferenceGeneratorUtil.generateReference()).merchantId(merchantId).amount(request.amount()).currency(request.currency()).status(PayoutStatus.PENDING).nextRetryAt(LocalDateTime.now()).build();
        payoutRepository.save(payout);
        webhookEventService.publishPayoutEvent(payout, SettlementWebhookEventType.PAYOUT_CREATED);
        log.info("Payout queued payoutId={} merchantId={} amount={}", payout.getId(), merchantId, request.amount());
        return map(payout);
    }

    private PayoutResponse map(Payout payout) {
        return new PayoutResponse(payout.getId(), payout.getPayoutReference(), payout.getMerchantId(), payout.getAmount(), payout.getCurrency(), payout.getStatus(), payout.getBankReference(), payout.getFailureReason(), payout.getProcessedAt(), payout.getCreatedAt());
    }
}