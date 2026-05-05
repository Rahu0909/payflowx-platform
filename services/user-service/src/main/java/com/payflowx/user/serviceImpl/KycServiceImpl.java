package com.payflowx.user.serviceImpl;

import com.payflowx.user.constant.ErrorCode;
import com.payflowx.user.dto.KycResponse;
import com.payflowx.user.dto.SubmitKycRequest;
import com.payflowx.user.entity.User;
import com.payflowx.user.entity.UserKyc;
import com.payflowx.user.entity.enums.KycStatus;
import com.payflowx.user.exception.BusinessValidationException;
import com.payflowx.user.exception.ResourceNotFoundException;
import com.payflowx.user.mapper.UserMapper;
import com.payflowx.user.repository.UserRepository;
import com.payflowx.user.service.KycService;
import com.payflowx.user.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycServiceImpl implements KycService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    /* ---------------- SUBMIT ---------------- */
    @Override
    @Transactional
    public KycResponse submitKyc(UUID authUserId, SubmitKycRequest request) {
        User user = getUser(authUserId);
        UserKyc kyc = user.getKyc();
        if (kyc != null && kyc.getKycStatus() == KycStatus.VERIFIED) {
            throw new BusinessValidationException(ErrorCode.KYC_ALREADY_VERIFIED);
        }
        if (kyc == null) {
            kyc = new UserKyc();
            user.setKyc(kyc);
        }
        kyc.setLegalName(request.legalName());
        kyc.setDateOfBirth(request.dateOfBirth());
        kyc.setNationality(request.nationality());
        kyc.setGovernmentId(request.governmentId());
        kyc.setKycStatus(KycStatus.PENDING);
        kyc.setApprovedAt(LocalDateTime.now());
        kyc.setApprovedBy(SecurityUtil.getCurrentUserId());
        kyc.setRejectionReason(null);
        log.info("KYC submitted for userId={}", user.getId());
        return mapper.toKycResponse(kyc);
    }

    /* ---------------- GET ---------------- */
    @Override
    public KycResponse getKyc(UUID authUserId) {
        User user = getUser(authUserId);
        if (user.getKyc() == null) {
            throw new BusinessValidationException(ErrorCode.KYC_NOT_FOUND);
        }
        return mapper.toKycResponse(user.getKyc());
    }

    /* ---------------- UPDATE ---------------- */
    @Override
    @Transactional
    public KycResponse updateKyc(UUID authUserId, SubmitKycRequest request) {
        User user = getUser(authUserId);
        UserKyc kyc = user.getKyc();
        if (kyc == null) {
            throw new BusinessValidationException(ErrorCode.KYC_NOT_FOUND);
        }
        if (kyc.getKycStatus() == KycStatus.VERIFIED) {
            throw new BusinessValidationException(ErrorCode.KYC_ALREADY_APPROVED);
        }
        kyc.setLegalName(request.legalName());
        kyc.setDateOfBirth(request.dateOfBirth());
        kyc.setNationality(request.nationality());
        kyc.setGovernmentId(request.governmentId());
        kyc.setKycStatus(KycStatus.PENDING);
        kyc.setApprovedAt(LocalDateTime.now());
        kyc.setApprovedBy(SecurityUtil.getCurrentUserId());
        kyc.setRejectionReason(null);
        log.info("KYC updated for userId={}", user.getId());
        return mapper.toKycResponse(kyc);
    }

    /* ---------------- APPROVE (ADMIN) ---------------- */
    @Override
    @Transactional
    public void approveKyc(UUID userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        UserKyc kyc = user.getKyc();
        if (kyc == null) {
            throw new BusinessValidationException(ErrorCode.KYC_NOT_FOUND);
        }
        if (kyc.getKycStatus() == KycStatus.VERIFIED) {
            throw new BusinessValidationException(ErrorCode.KYC_ALREADY_APPROVED);
        }
        if (kyc.getKycStatus() == KycStatus.REJECTED) {
            throw new BusinessValidationException(ErrorCode.KYC_REJECTED_RESUBMIT_REQUIRED);
        }
        kyc.setKycStatus(KycStatus.VERIFIED);
        kyc.setVerifiedAt(LocalDateTime.now());
        kyc.setApprovedAt(LocalDateTime.now());
        kyc.setApprovedBy(SecurityUtil.getCurrentUserId());
        kyc.setRejectionReason(null); // CLEANUP
        user.setVerified(true);
        log.info("KYC approved for userId={}", userId);
    }

    /* ---------------- REJECT (ADMIN) ---------------- */
    @Override
    @Transactional
    public void rejectKyc(UUID userId, String reason) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        UserKyc kyc = user.getKyc();
        if (kyc == null) {
            throw new BusinessValidationException(ErrorCode.KYC_NOT_FOUND);
        }
        if (kyc.getKycStatus() == KycStatus.VERIFIED) {
            throw new BusinessValidationException(ErrorCode.KYC_REJECTED_RESUBMIT_REQUIRED);
        }
        if (kyc.getKycStatus() == KycStatus.REJECTED) {
            throw new BusinessValidationException(ErrorCode.KYC_ALREADY_REJECTED);
        }
        kyc.setKycStatus(KycStatus.REJECTED);
        kyc.setRejectionReason(reason);
        kyc.setApprovedAt(LocalDateTime.now());
        kyc.setApprovedBy(SecurityUtil.getCurrentUserId());
        kyc.setVerifiedAt(null); // CLEANUP
        log.warn("KYC rejected for userId={}, reason={}", userId, reason);
    }

    private User getUser(UUID authUserId) {
        return userRepository.findByAuthUserIdAndDeletedFalse(authUserId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
    }
}