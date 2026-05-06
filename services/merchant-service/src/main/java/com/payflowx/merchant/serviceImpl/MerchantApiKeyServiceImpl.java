package com.payflowx.merchant.serviceImpl;

import com.payflowx.merchant.constant.ErrorCode;
import com.payflowx.merchant.dto.request.CreateApiKeyRequest;
import com.payflowx.merchant.dto.request.ValidateApiKeyRequest;
import com.payflowx.merchant.dto.response.ApiKeyResponse;
import com.payflowx.merchant.dto.response.ValidateApiKeyResponse;
import com.payflowx.merchant.entity.Merchant;
import com.payflowx.merchant.entity.MerchantApiKey;
import com.payflowx.merchant.enums.ApiKeyStatus;
import com.payflowx.merchant.enums.KycStatus;
import com.payflowx.merchant.enums.MerchantStatus;
import com.payflowx.merchant.exception.BusinessValidationException;
import com.payflowx.merchant.repository.MerchantApiKeyRepository;
import com.payflowx.merchant.repository.MerchantKycRepository;
import com.payflowx.merchant.repository.MerchantRepository;
import com.payflowx.merchant.service.MerchantApiKeyService;
import com.payflowx.merchant.util.ApiKeyGeneratorUtil;
import com.payflowx.merchant.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MerchantApiKeyServiceImpl implements MerchantApiKeyService {

    private static final long MAX_KEYS = 5;

    private final MerchantRepository merchantRepository;
    private final MerchantKycRepository kycRepository;
    private final MerchantApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiKeyResponse createApiKey(UUID authUserId, CreateApiKeyRequest request) {
        Merchant merchant = validateMerchant(authUserId);
        long totalKeys = apiKeyRepository.countByMerchantIdAndDeletedFalse(merchant.getId());
        if (totalKeys >= MAX_KEYS) {
            throw new BusinessValidationException(ErrorCode.MAX_API_KEYS_LIMIT_REACHED);
        }
        String publicKey = ApiKeyGeneratorUtil.generatePublicKey(request.environment());
        String secretKey = ApiKeyGeneratorUtil.generateSecretKey(request.environment());
        MerchantApiKey apiKey = MerchantApiKey.builder().merchant(merchant).publicKey(publicKey).secretKeyHash(passwordEncoder.encode(secretKey)).keyPrefix(ApiKeyGeneratorUtil.extractKeyPrefix(secretKey)).environment(request.environment()).status(ApiKeyStatus.ACTIVE).build();
        MerchantApiKey saved = apiKeyRepository.save(apiKey);
        log.info("API key created merchantId={} apiKeyId={}", merchant.getId(), saved.getId());
        return map(saved, secretKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyResponse> getApiKeys(UUID authUserId) {
        Merchant merchant = validateMerchant(authUserId);
        return apiKeyRepository.findByMerchantIdAndDeletedFalse(merchant.getId()).stream().map(key -> map(key, null)).toList();
    }

    @Override
    public ApiKeyResponse rotateApiKey(UUID authUserId, UUID apiKeyId) {
        Merchant merchant = validateMerchant(authUserId);
        MerchantApiKey oldKey = apiKeyRepository.findById(apiKeyId).orElseThrow(() -> new BusinessValidationException(ErrorCode.API_KEY_NOT_FOUND));
        validateOwnership(oldKey, merchant);
        oldKey.setStatus(ApiKeyStatus.REVOKED);
        oldKey.setRevokedAt(LocalDateTime.now());
        oldKey.setRevokedBy(SecurityUtil.getCurrentUserId());
        String newSecret = ApiKeyGeneratorUtil.generateSecretKey(oldKey.getEnvironment());
        MerchantApiKey newKey = MerchantApiKey.builder().merchant(merchant).publicKey(ApiKeyGeneratorUtil.generatePublicKey(oldKey.getEnvironment())).secretKeyHash(passwordEncoder.encode(newSecret)).keyPrefix(ApiKeyGeneratorUtil.extractKeyPrefix(newSecret)).environment(oldKey.getEnvironment()).status(ApiKeyStatus.ACTIVE).build();
        MerchantApiKey saved = apiKeyRepository.save(newKey);
        log.info("API key rotated merchantId={} oldKey={} newKey={}", merchant.getId(), oldKey.getId(), saved.getId());
        return map(saved, newSecret);
    }

    @Override
    public void revokeApiKey(UUID authUserId, UUID apiKeyId) {
        Merchant merchant = validateMerchant(authUserId);
        MerchantApiKey apiKey = apiKeyRepository.findById(apiKeyId).orElseThrow(() -> new BusinessValidationException(ErrorCode.API_KEY_NOT_FOUND));
        validateOwnership(apiKey, merchant);
        apiKey.setStatus(ApiKeyStatus.REVOKED);
        apiKey.setRevokedAt(LocalDateTime.now());
        apiKey.setRevokedBy(SecurityUtil.getCurrentUserId());
        apiKey.setDeleted(true);
        apiKey.setDeletedAt(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
        log.warn("API key revoked merchantId={} apiKeyId={}", merchant.getId(), apiKey.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateApiKeyResponse validateSecretKey(ValidateApiKeyRequest request) {
        String incomingKey = request.secretKey();
        String prefix = ApiKeyGeneratorUtil.extractKeyPrefix(incomingKey);
        List<MerchantApiKey> keys = apiKeyRepository.findAll();
        for (MerchantApiKey key : keys) {
            if (!key.getKeyPrefix().equals(prefix)) {
                continue;
            }
            if (key.getStatus() == ApiKeyStatus.REVOKED) {
                throw new BusinessValidationException(ErrorCode.API_KEY_REVOKED);
            }
            boolean matches = passwordEncoder.matches(incomingKey, key.getSecretKeyHash());
            if (!matches) {
                continue;
            }
            Merchant merchant = key.getMerchant();
            if (merchant.getStatus() != MerchantStatus.ACTIVE) {
                return new ValidateApiKeyResponse(false, merchant.getId(), "MERCHANT_NOT_ACTIVE");
            }
            key.setLastUsedAt(LocalDateTime.now());
            return new ValidateApiKeyResponse(true, merchant.getId(), "VALID");
        }
        throw new BusinessValidationException(ErrorCode.INVALID_API_KEY);
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

    private void validateOwnership(MerchantApiKey apiKey, Merchant merchant) {
        if (!apiKey.getMerchant().getId().equals(merchant.getId())) {
            throw new BusinessValidationException(ErrorCode.INVALID_API_KEY);
        }
    }

    private ApiKeyResponse map(MerchantApiKey key, String secret) {
        return new ApiKeyResponse(key.getId(),
                key.getPublicKey(),
                secret,
                key.getKeyPrefix(),
                key.getEnvironment().name(),
                key.getStatus().name(),
                key.getCreatedAt(),
                key.getLastUsedAt());
    }
}