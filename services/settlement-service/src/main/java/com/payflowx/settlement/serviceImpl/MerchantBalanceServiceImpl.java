package com.payflowx.settlement.serviceImpl;

import com.payflowx.settlement.constant.ErrorCode;
import com.payflowx.settlement.dto.response.MerchantBalanceResponse;
import com.payflowx.settlement.entity.MerchantBalance;
import com.payflowx.settlement.exception.BusinessValidationException;
import com.payflowx.settlement.mapper.MerchantBalanceMapper;
import com.payflowx.settlement.repository.MerchantBalanceRepository;
import com.payflowx.settlement.service.MerchantBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantBalanceServiceImpl implements MerchantBalanceService {

    private final MerchantBalanceRepository merchantBalanceRepository;
    private final MerchantBalanceMapper merchantBalanceMapper;

    @Override
    @Transactional
    public MerchantBalance getOrCreateBalance(UUID merchantId) {
        return merchantBalanceRepository.findByMerchantId(merchantId).orElseGet(() -> createBalance(merchantId));
    }

    private MerchantBalance createBalance(UUID merchantId) {
        try {
            MerchantBalance balance = MerchantBalance.builder().merchantId(merchantId).build();
            merchantBalanceRepository.save(balance);
            log.info("Merchant balance created merchantId={}", merchantId);
            return balance;
        } catch (DataIntegrityViolationException ex) {
            /*
             * Concurrent creation protection
             */
            return merchantBalanceRepository.findByMerchantId(merchantId).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_BALANCE_NOT_FOUND));
        }
    }

    @Override
    @Transactional
    public void addPendingBalance(UUID merchantId, BigDecimal amount) {
        validatePositiveAmount(amount);
        MerchantBalance balance = getOrCreateBalance(merchantId);
        balance.setPendingBalance(balance.getPendingBalance().add(amount));
        merchantBalanceRepository.save(balance);
        log.info("Pending balance added merchantId={} amount={}", merchantId, amount);
    }

    @Override
    @Transactional
    public void movePendingToAvailable(UUID merchantId, BigDecimal amount) {
        validatePositiveAmount(amount);
        MerchantBalance balance = getOrCreateBalance(merchantId);
        if (balance.getPendingBalance().compareTo(amount) < 0) {
            throw new BusinessValidationException(ErrorCode.INSUFFICIENT_AVAILABLE_BALANCE);
        }
        balance.setPendingBalance(balance.getPendingBalance().subtract(amount));
        balance.setAvailableBalance(balance.getAvailableBalance().add(amount));
        merchantBalanceRepository.save(balance);
        log.info("Pending moved to available merchantId={} amount={}", merchantId, amount);
    }

    @Override
    @Transactional
    public void deductAvailableBalance(UUID merchantId, BigDecimal amount) {
        validatePositiveAmount(amount);
        MerchantBalance balance = getOrCreateBalance(merchantId);
        if (balance.getAvailableBalance().compareTo(amount) < 0) {
            throw new BusinessValidationException(ErrorCode.INSUFFICIENT_AVAILABLE_BALANCE);
        }
        balance.setAvailableBalance(balance.getAvailableBalance().subtract(amount));
        balance.setSettledBalance(balance.getSettledBalance().add(amount));
        merchantBalanceRepository.save(balance);
        log.info("Available balance settled merchantId={} amount={}", merchantId, amount);
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantBalanceResponse getBalance(UUID merchantId) {
        MerchantBalance balance = merchantBalanceRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_BALANCE_NOT_FOUND));
        return merchantBalanceMapper.toResponse(balance);
    }
    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException(ErrorCode.INVALID_SETTLEMENT_AMOUNT);
        }
    }
}