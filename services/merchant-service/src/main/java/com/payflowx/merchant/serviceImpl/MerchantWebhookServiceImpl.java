package com.payflowx.merchant.serviceImpl;

import com.payflowx.merchant.constant.ErrorCode;
import com.payflowx.merchant.dto.request.CreateWebhookRequest;
import com.payflowx.merchant.dto.request.UpdateWebhookRequest;
import com.payflowx.merchant.dto.request.WebhookSignatureRequest;
import com.payflowx.merchant.dto.response.WebhookResponse;
import com.payflowx.merchant.dto.response.WebhookSignatureResponse;
import com.payflowx.merchant.entity.Merchant;
import com.payflowx.merchant.entity.MerchantWebhook;
import com.payflowx.merchant.enums.KycStatus;
import com.payflowx.merchant.enums.MerchantStatus;
import com.payflowx.merchant.exception.BusinessValidationException;
import com.payflowx.merchant.mapper.WebhookMapper;
import com.payflowx.merchant.repository.MerchantKycRepository;
import com.payflowx.merchant.repository.MerchantRepository;
import com.payflowx.merchant.repository.MerchantWebhookRepository;
import com.payflowx.merchant.service.MerchantWebhookService;
import com.payflowx.merchant.util.WebhookSecretUtil;
import com.payflowx.merchant.util.WebhookSignatureUtil;
import com.payflowx.merchant.util.WebhookValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MerchantWebhookServiceImpl implements MerchantWebhookService {

    private static final long MAX_WEBHOOKS = 5;
    private final MerchantRepository merchantRepository;
    private final MerchantKycRepository kycRepository;
    private final MerchantWebhookRepository webhookRepository;
    private final WebhookMapper mapper;

    @Override
    public WebhookResponse createWebhook(UUID authUserId, CreateWebhookRequest request) {
        Merchant merchant = validateMerchant(authUserId);
        long totalWebhooks = webhookRepository.countByMerchantIdAndDeletedFalse(merchant.getId());
        if (totalWebhooks >= MAX_WEBHOOKS) {
            throw new BusinessValidationException(ErrorCode.WEBHOOK_LIMIT_EXCEEDED);
        }
        WebhookValidationUtil.validateWebhookUrl(request.webhookUrl());
        MerchantWebhook webhook = MerchantWebhook.builder().merchant(merchant).webhookUrl(request.webhookUrl()).webhookSecret(WebhookSecretUtil.generateSecret()).subscribedEvents(request.subscribedEvents()).build();
        MerchantWebhook saved = webhookRepository.save(webhook);
        log.info("Webhook created merchantId={} webhookId={}", merchant.getId(), saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebhookResponse> getWebhooks(UUID authUserId) {
        Merchant merchant = validateMerchant(authUserId);
        return webhookRepository.findByMerchantIdAndDeletedFalse(merchant.getId()).stream().map(mapper::toResponse).toList();
    }

    @Override
    public WebhookResponse updateWebhook(UUID authUserId, UUID webhookId, UpdateWebhookRequest request) {
        Merchant merchant = validateMerchant(authUserId);
        MerchantWebhook webhook = webhookRepository.findByIdAndDeletedFalse(webhookId).orElseThrow(() -> new BusinessValidationException(ErrorCode.WEBHOOK_NOT_FOUND));
        validateOwnership(webhook, merchant);
        webhook.setStatus(request.status());
        webhook.setSubscribedEvents(request.subscribedEvents());
        MerchantWebhook updated = webhookRepository.save(webhook);
        log.info("Webhook updated merchantId={} webhookId={}", merchant.getId(), webhook.getId());
        return mapper.toResponse(updated);
    }

    @Override
    public void deleteWebhook(UUID authUserId, UUID webhookId) {
        Merchant merchant = validateMerchant(authUserId);
        MerchantWebhook webhook = webhookRepository.findByIdAndDeletedFalse(webhookId).orElseThrow(() -> new BusinessValidationException(ErrorCode.WEBHOOK_NOT_FOUND));
        validateOwnership(webhook, merchant);
        webhook.setDeleted(true);
        webhook.setDeletedAt(LocalDateTime.now());
        webhookRepository.save(webhook);
        log.warn("Webhook deleted merchantId={} webhookId={}", merchant.getId(), webhook.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public WebhookSignatureResponse generateSignature(WebhookSignatureRequest request) {
        MerchantWebhook webhook = webhookRepository.findByIdAndDeletedFalse(request.webhookId()).orElseThrow(() -> new BusinessValidationException(ErrorCode.WEBHOOK_NOT_FOUND));
        String signature = WebhookSignatureUtil.sign(request.payload(), webhook.getWebhookSecret());
        return new WebhookSignatureResponse(signature);
    }

    private Merchant validateMerchant(UUID authUserId) {
        Merchant merchant = merchantRepository.findByAuthUserIdAndDeletedFalse(authUserId).orElseThrow(() -> new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND));
        if (merchant.getStatus() != MerchantStatus.ACTIVE) {
            throw new BusinessValidationException(ErrorCode.MERCHANT_NOT_ACTIVE);
        }

        var kyc = kycRepository.findByMerchantId(merchant.getId()).orElseThrow(() -> new BusinessValidationException(ErrorCode.KYC_NOT_FOUND));
        if (kyc.getKycStatus() != KycStatus.VERIFIED) {
            throw new BusinessValidationException(ErrorCode.KYC_NOT_VERIFIED);
        }
        return merchant;
    }

    private void validateOwnership(MerchantWebhook webhook, Merchant merchant) {
        if (!webhook.getMerchant().getId().equals(merchant.getId())) {
            throw new BusinessValidationException(ErrorCode.WEBHOOK_NOT_FOUND);
        }
    }
}