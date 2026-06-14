package com.payflowx.payment.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflowx.payment.client.OrderClient;
import com.payflowx.payment.constant.ErrorCode;
import com.payflowx.payment.dto.ApiResponse;
import com.payflowx.payment.dto.response.FeignErrorResponse;
import com.payflowx.payment.dto.response.InternalOrderValidationResponse;
import com.payflowx.payment.exception.BusinessValidationException;
import com.payflowx.payment.service.OrderValidationService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderValidationServiceImpl implements OrderValidationService {
    private final OrderClient orderClient;
    private final ObjectMapper objectMapper;

    @Override
    @CircuitBreaker(name = "order-service", fallbackMethod = "validateOrderFallback")
    @Retry(name = "order-service")
    public InternalOrderValidationResponse validateOrder(UUID orderId, BigDecimal amount, String currency) {
        try {
            ApiResponse<InternalOrderValidationResponse> response = orderClient.validateOrder(orderId);
            if (response == null || response.data() == null) {
                throw new BusinessValidationException(ErrorCode.ORDER_NOT_FOUND);
            }
            InternalOrderValidationResponse order = response.data();
            /*
             * Amount validation
             */
            if (order.amount().compareTo(amount) != 0) {
                throw new BusinessValidationException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
            }
            /*
             * Currency validation
             */
            if (!order.currency().equals(currency)) {
                throw new BusinessValidationException(ErrorCode.PAYMENT_CURRENCY_MISMATCH);
            }
            return order;
        } catch (FeignException ex) {
            handleFeignException(ex);
            throw ex;
        }
    }

    @Override
    @CircuitBreaker(name = "order-service", fallbackMethod = "markOrderPaidFallback")
    @Retry(name = "order-service")
    public void markOrderPaid(UUID orderId) {
        try {
            orderClient.markOrderPaid(orderId);
        } catch (FeignException ex) {
            handleFeignException(ex);
            throw ex;
        }
    }

    private void handleFeignException(FeignException ex) {
        try {
            String responseBody = ex.contentUTF8();
            log.error("Feign client error response={}", responseBody);
            FeignErrorResponse errorResponse = objectMapper.readValue(responseBody, FeignErrorResponse.class);
            String errorMessage = errorResponse.message();
            if (errorMessage == null || errorMessage.isBlank()) {
                throw new BusinessValidationException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            errorMessage = errorMessage.trim();
            /*
             * Validate enum existence safely
             */
            for (ErrorCode errorCode : ErrorCode.values()) {
                if (errorCode.name().equals(errorMessage)) {
                    throw new BusinessValidationException(errorCode);
                }
            }
            throw new BusinessValidationException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (BusinessValidationException ex2) {
            throw ex2;
        } catch (Exception ignored) {
            throw new BusinessValidationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private InternalOrderValidationResponse validateOrderFallback(UUID orderId, BigDecimal amount, String currency, Exception ex) {
        log.error("Order validation failed. orderId={}", orderId, ex);
        throw new BusinessValidationException(ErrorCode.ORDER_SERVICE_UNAVAILABLE);
    }

    private void markOrderPaidFallback(UUID orderId, Exception ex) {
        log.error("Mark order paid failed. orderId={}", orderId, ex);
        throw new BusinessValidationException(ErrorCode.ORDER_SERVICE_UNAVAILABLE);
    }
}