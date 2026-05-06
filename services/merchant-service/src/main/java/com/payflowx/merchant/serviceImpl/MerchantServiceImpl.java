package com.payflowx.merchant.serviceImpl;

import com.payflowx.merchant.constant.ErrorCode;
import com.payflowx.merchant.dto.request.CreateMerchantRequest;
import com.payflowx.merchant.dto.request.UpdateMerchantStatusRequest;
import com.payflowx.merchant.dto.response.AdminMerchantResponse;
import com.payflowx.merchant.dto.response.MerchantResponse;
import com.payflowx.merchant.entity.Merchant;
import com.payflowx.merchant.enums.MerchantStatus;
import com.payflowx.merchant.exception.BusinessValidationException;
import com.payflowx.merchant.mapper.MerchantMapper;
import com.payflowx.merchant.repository.MerchantRepository;
import com.payflowx.merchant.service.MerchantService;
import com.payflowx.merchant.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantMapper mapper;

    @Override
    public MerchantResponse createMerchant(UUID authUserId, CreateMerchantRequest request) {
        log.info("Creating merchant for user: {}", authUserId);
        if (merchantRepository.existsByEmail(request.email())) {
            throw new BusinessValidationException(ErrorCode.MERCHANT_ALREADY_EXISTS);
        }
        if (merchantRepository.findByAuthUserIdAndDeletedFalse(authUserId).isPresent()) {
            throw new BusinessValidationException(ErrorCode.MERCHANT_ALREADY_EXISTS);
        }
        Merchant merchant = Merchant.builder().authUserId(authUserId).businessName(request.businessName())
                .email(request.email())
                .phone(request.phone())
                .category(request.category())
                .status(MerchantStatus.SUSPENDED)
                .statusChangedAt(LocalDateTime.now())
                .statusReason("INITIAL_CREATION").build();
        Merchant saved = merchantRepository.save(merchant);
        log.info("Merchant created with id: {}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    public MerchantResponse getMerchantByUser(UUID authUserId) {
        Merchant merchant = merchantRepository
                .findByAuthUserIdAndDeletedFalse(authUserId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND));
        return mapper.toResponse(merchant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminMerchantResponse> getAllMerchants(Pageable pageable) {
        return merchantRepository.findAllByDeletedFalse(pageable).map(mapper::toAdminResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminMerchantResponse getMerchantById(UUID merchantId) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(merchantId).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND));
        return mapper.toAdminResponse(merchant);
    }

    @Override
    @Transactional
    public AdminMerchantResponse updateMerchantStatus(UUID merchantId, UpdateMerchantStatusRequest request) {

        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(merchantId).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND));
        MerchantStatus currentStatus = merchant.getStatus();
        MerchantStatus newStatus = request.status();
        /* same status validation*/
        if (currentStatus == newStatus) {
            throw new BusinessValidationException(ErrorCode.STATUS_ALREADY_SET);
        }
        /*  BLOCKED → ACTIVE not allowed directly*/
        if (currentStatus == MerchantStatus.BLOCKED && newStatus == MerchantStatus.ACTIVE) {
            throw new BusinessValidationException(ErrorCode.BLOCKED_MERCHANT_MUST_BE_SUSPENDED_FIRST);
        }
        merchant.setStatus(newStatus);
        merchant.setStatusChangedAt(LocalDateTime.now());
        merchant.setStatusChangedBy(SecurityUtil.getCurrentUserId());
        merchant.setStatusReason(request.reason());
        Merchant updated = merchantRepository.save(merchant);
        log.info("Merchant status updated merchantId={} old={} new={}", merchantId, currentStatus, newStatus);
        return mapper.toAdminResponse(updated);
    }

    @Override
    @Transactional
    public void deactivateMerchant(UUID merchantId) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(merchantId).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND));
        merchant.setDeleted(true);
        merchant.setDeletedAt(LocalDateTime.now());
        merchantRepository.save(merchant);
        log.warn("Merchant deactivated merchantId={}", merchantId);
    }
}