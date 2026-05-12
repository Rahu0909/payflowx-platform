package com.payflowx.merchant.serviceImpl;

import com.payflowx.merchant.constant.ErrorCode;
import com.payflowx.merchant.dto.request.UpdateMerchantSettlementConfigRequest;
import com.payflowx.merchant.dto.response.InternalMerchantSettlementConfigResponse;
import com.payflowx.merchant.dto.response.MerchantSettlementConfigResponse;
import com.payflowx.merchant.entity.Merchant;
import com.payflowx.merchant.entity.MerchantSettlementConfig;
import com.payflowx.merchant.exception.BusinessValidationException;
import com.payflowx.merchant.repository.MerchantRepository;
import com.payflowx.merchant.repository.MerchantSettlementConfigRepository;
import com.payflowx.merchant.service.MerchantSettlementConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantSettlementConfigServiceImpl implements MerchantSettlementConfigService {


    private final MerchantSettlementConfigRepository repository;

    private final MerchantRepository merchantRepository;

    @Override
    @Transactional(readOnly = true)
    public InternalMerchantSettlementConfigResponse getSettlementConfig(UUID merchantId) {
        MerchantSettlementConfig config = repository.findByMerchantId(merchantId).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND));
        return new InternalMerchantSettlementConfigResponse(merchantId, config.getPlatformFeePercentage(), config.getSettlementDelayDays(), config.getRollingReservePercentage(), config.getMinimumPayoutAmount(), config.getSettlementEnabled());
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantSettlementConfigResponse getMerchantSettlementConfig(UUID authUserId) {
        Merchant merchant = merchantRepository.findByAuthUserIdAndDeletedFalse(authUserId).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND));
        MerchantSettlementConfig config = getConfigEntity(merchant.getId());
        return map(config);
    }

    @Override
    @Transactional
    public MerchantSettlementConfigResponse updateSettlementConfig(UUID merchantId, UpdateMerchantSettlementConfigRequest request) {
        MerchantSettlementConfig config = getConfigEntity(merchantId);
        config.setPlatformFeePercentage(request.platformFeePercentage());
        config.setSettlementDelayDays(request.settlementDelayDays());
        config.setRollingReservePercentage(request.rollingReservePercentage());
        config.setMinimumPayoutAmount(request.minimumPayoutAmount());
        config.setSettlementEnabled(request.settlementEnabled());
        repository.save(config);
        log.info("Settlement config updated merchantId={}", merchantId);
        return map(config);
    }

    private MerchantSettlementConfig getConfigEntity(UUID merchantId) {
        return repository.findByMerchantId(merchantId).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND));
    }

    private MerchantSettlementConfigResponse map(MerchantSettlementConfig config) {
        return new MerchantSettlementConfigResponse(config.getPlatformFeePercentage(), config.getSettlementDelayDays(), config.getRollingReservePercentage(), config.getMinimumPayoutAmount(), config.getSettlementEnabled());
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantSettlementConfigResponse getAdminSettlementConfig(UUID merchantId) {
        return map(getConfigEntity(merchantId));
    }
}