package com.payflowx.order.serviceImpl;

import com.payflowx.order.client.MerchantClient;
import com.payflowx.order.constant.ErrorCode;
import com.payflowx.order.dto.ApiResponse;
import com.payflowx.order.dto.response.InternalMerchantValidationResponse;
import com.payflowx.order.exception.BusinessValidationException;
import com.payflowx.order.service.MerchantValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantValidationServiceImpl implements MerchantValidationService {
    private final MerchantClient merchantClient;
    @Override
    public InternalMerchantValidationResponse validateMerchant(UUID userId) {
        ApiResponse<InternalMerchantValidationResponse> response = merchantClient.validateMerchant(userId);
        if (response == null || response.data() == null) {
            throw new BusinessValidationException(ErrorCode.MERCHANT_NOT_FOUND);
        }
        return response.data();
    }
}