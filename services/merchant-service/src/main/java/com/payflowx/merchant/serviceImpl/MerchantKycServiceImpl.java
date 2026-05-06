package com.payflowx.merchant.serviceImpl;

import com.payflowx.merchant.constant.ErrorCode;
import com.payflowx.merchant.dto.request.RejectMerchantKycRequest;
import com.payflowx.merchant.dto.request.SubmitMerchantKycRequest;
import com.payflowx.merchant.dto.response.MerchantKycResponse;
import com.payflowx.merchant.entity.Merchant;
import com.payflowx.merchant.entity.MerchantKyc;
import com.payflowx.merchant.enums.KycStatus;
import com.payflowx.merchant.enums.MerchantStatus;
import com.payflowx.merchant.exception.BusinessValidationException;
import com.payflowx.merchant.repository.MerchantKycRepository;
import com.payflowx.merchant.repository.MerchantRepository;
import com.payflowx.merchant.service.MerchantKycService;
import com.payflowx.merchant.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantKycServiceImpl implements MerchantKycService {

    private final MerchantRepository merchantRepository;
    private final MerchantKycRepository kycRepository;

    @Override
    public MerchantKycResponse submitKyc(UUID authUserId, SubmitMerchantKycRequest request) {
        Merchant merchant = merchantRepository.findByAuthUserIdAndDeletedFalse(authUserId).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND));
        MerchantKyc kyc = kycRepository.findByMerchantId(merchant.getId()).orElse(MerchantKyc.builder().merchant(merchant).build());
        if (kyc.getKycStatus() == KycStatus.VERIFIED) {
            throw new BusinessValidationException(ErrorCode.KYC_ALREADY_VERIFIED);
        }
        kyc.setBusinessRegistrationNumber(request.businessRegistrationNumber());
        kyc.setTaxId(request.taxId());
        kyc.setBusinessPan(request.businessPan());
        kyc.setDocumentUrl(request.documentUrl());
        kyc.setKycStatus(KycStatus.PENDING);
        kyc.setReviewedAt(null);
        kyc.setReviewedBy(null);
        kyc.setRejectionReason(null);
        MerchantKyc saved = kycRepository.save(kyc);
        log.info("Merchant KYC submitted merchantId={}", merchant.getId());
        return map(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantKycResponse getKyc(UUID authUserId) {
        Merchant merchant = merchantRepository.findByAuthUserIdAndDeletedFalse(authUserId).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND));
        MerchantKyc kyc = kycRepository.findByMerchantId(merchant.getId()).orElseThrow(() -> new BusinessValidationException(ErrorCode.KYC_NOT_FOUND));
        return map(kyc);
    }

    @Override
    @Transactional
    public MerchantKycResponse approveKyc(UUID merchantId) {
        MerchantKyc kyc = kycRepository.findByMerchantId(merchantId).orElseThrow(() -> new BusinessValidationException(ErrorCode.KYC_NOT_FOUND));
        if (kyc.getKycStatus() == KycStatus.VERIFIED) {
            throw new BusinessValidationException(ErrorCode.KYC_ALREADY_APPROVED);
        }
        kyc.setKycStatus(KycStatus.VERIFIED);
        kyc.setReviewedAt(LocalDateTime.now());
        kyc.setReviewedBy(SecurityUtil.getCurrentUserId());
        kyc.setRejectionReason(null);
        Merchant merchant = kyc.getMerchant();
        merchant.setStatus(MerchantStatus.ACTIVE);
        merchant.setStatusChangedAt(LocalDateTime.now());
        merchant.setStatusChangedBy(SecurityUtil.getCurrentUserId());
        merchant.setStatusReason("KYC_APPROVED");
        kycRepository.save(kyc);
        log.info("Merchant KYC approved merchantId={}", merchantId);
        return map(kyc);
    }

    @Override
    @Transactional
    public MerchantKycResponse rejectKyc(UUID merchantId, RejectMerchantKycRequest request) {
        MerchantKyc kyc = kycRepository.findByMerchantId(merchantId).orElseThrow(() -> new BusinessValidationException(ErrorCode.KYC_NOT_FOUND));
        if (kyc.getKycStatus() == KycStatus.REJECTED) {
            throw new BusinessValidationException(ErrorCode.KYC_ALREADY_REJECTED);
        }
        kyc.setKycStatus(KycStatus.REJECTED);
        kyc.setReviewedAt(LocalDateTime.now());
        kyc.setReviewedBy(SecurityUtil.getCurrentUserId());
        kyc.setRejectionReason(request.reason());
        Merchant merchant = kyc.getMerchant();
        merchant.setStatus(MerchantStatus.SUSPENDED);
        merchant.setStatusChangedAt(LocalDateTime.now());
        merchant.setStatusChangedBy(SecurityUtil.getCurrentUserId());
        merchant.setStatusReason("KYC_REJECTED");
        kycRepository.save(kyc);
        log.warn("Merchant KYC rejected merchantId={}", merchantId);
        return map(kyc);
    }

    private MerchantKycResponse map(MerchantKyc kyc) {
        return new MerchantKycResponse(kyc.getBusinessRegistrationNumber(), kyc.getTaxId(), kyc.getBusinessPan(), kyc.getDocumentUrl(), kyc.getKycStatus().name(), kyc.getRejectionReason());
    }
}