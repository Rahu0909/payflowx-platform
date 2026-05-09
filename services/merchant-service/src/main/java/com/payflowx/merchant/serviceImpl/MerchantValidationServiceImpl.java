package com.payflowx.merchant.serviceImpl;

import com.payflowx.merchant.constant.ErrorCode;
import com.payflowx.merchant.dto.response.InternalMerchantValidationResponse;
import com.payflowx.merchant.entity.Merchant;
import com.payflowx.merchant.entity.MerchantKyc;
import com.payflowx.merchant.enums.KycStatus;
import com.payflowx.merchant.enums.MerchantStatus;
import com.payflowx.merchant.exception.BusinessValidationException;
import com.payflowx.merchant.repository.MerchantKycRepository;
import com.payflowx.merchant.repository.MerchantRepository;
import com.payflowx.merchant.service.MerchantValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantValidationServiceImpl implements MerchantValidationService {
    private final MerchantRepository merchantRepository;
    private final MerchantKycRepository merchantKycRepository;
    @Override
    public InternalMerchantValidationResponse validateMerchant(UUID userId) {
        Merchant merchant = merchantRepository.findByAuthUserIdAndDeletedFalse(userId).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND));
        if (merchant.getStatus() != MerchantStatus.ACTIVE) {
            throw new BusinessValidationException(ErrorCode.MERCHANT_NOT_ACTIVE);
        }
        MerchantKyc merchantKyc = merchantKycRepository.findByMerchantId(merchant.getId()).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_KYC_NOT_FOUND));
        boolean kycVerified = merchantKyc.getKycStatus() == KycStatus.VERIFIED;
        if (!kycVerified) {
            throw new BusinessValidationException(ErrorCode.MERCHANT_NOT_ACTIVE);
        }
        return new InternalMerchantValidationResponse(true, merchant.getId().toString(), merchant.getBusinessName(), merchant.getStatus().name(), true);
    }
}