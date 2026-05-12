package com.payflowx.payment.serviceImpl;

import com.payflowx.payment.client.MerchantClient;
import com.payflowx.payment.constant.ErrorCode;
import com.payflowx.payment.dto.ApiResponse;
import com.payflowx.payment.dto.response.InternalMerchantSettlementConfigResponse;
import com.payflowx.payment.dto.response.PaymentFinancialDetails;
import com.payflowx.payment.exception.BusinessValidationException;
import com.payflowx.payment.service.PaymentFinancialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentFinancialServiceImpl implements PaymentFinancialService {

    private final MerchantClient merchantClient;

    @Override
    @Transactional(readOnly = true)
    public PaymentFinancialDetails calculateFinancials(
            UUID merchantId,
            BigDecimal grossAmount) {
        ApiResponse<InternalMerchantSettlementConfigResponse> response = merchantClient.getSettlementConfig(merchantId);
        if (response == null || response.data() == null) {
            throw new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND);
        }
        InternalMerchantSettlementConfigResponse config = response.data();
        if (!config.settlementEnabled()) {
            throw new BusinessValidationException(ErrorCode.INVALID_REQUEST);
        }
        BigDecimal platformFeeAmount = calculatePercentage(grossAmount, config.platformFeePercentage());
        BigDecimal reserveAmount = calculatePercentage(grossAmount, config.rollingReservePercentage());
        BigDecimal netSettlementAmount = grossAmount.subtract(platformFeeAmount).subtract(reserveAmount);
        return new PaymentFinancialDetails(grossAmount, platformFeeAmount, reserveAmount, netSettlementAmount, config.settlementDelayDays());
    }

    private BigDecimal calculatePercentage(
            BigDecimal amount,
            BigDecimal percentage) {
        return amount.multiply(percentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}