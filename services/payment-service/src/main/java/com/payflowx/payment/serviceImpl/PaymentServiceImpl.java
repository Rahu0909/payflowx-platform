package com.payflowx.payment.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflowx.payment.constant.ErrorCode;
import com.payflowx.payment.dto.request.CreatePaymentRequest;
import com.payflowx.payment.dto.response.InternalOrderValidationResponse;
import com.payflowx.payment.dto.response.PaymentFinancialDetails;
import com.payflowx.payment.dto.response.PaymentGatewayResponse;
import com.payflowx.payment.dto.response.PaymentResponse;
import com.payflowx.payment.entity.Payment;
import com.payflowx.payment.entity.PaymentAttempt;
import com.payflowx.payment.entity.PaymentIdempotency;
import com.payflowx.payment.enums.Currency;
import com.payflowx.payment.enums.PaymentEventType;
import com.payflowx.payment.enums.PaymentStatus;
import com.payflowx.payment.exception.BusinessValidationException;
import com.payflowx.payment.mapper.PaymentMapper;
import com.payflowx.payment.processor.PaymentProcessor;
import com.payflowx.payment.repository.PaymentRepository;
import com.payflowx.payment.service.*;
import com.payflowx.payment.strategy.PaymentProcessorFactory;
import com.payflowx.payment.util.ReferenceGeneratorUtil;
import com.payflowx.payment.util.RequestHashUtil;
import com.payflowx.payment.util.SecurityUtil;
import com.payflowx.payment.validator.PaymentStateMachineValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderValidationService orderValidationService;
    private final IdempotencyService idempotencyService;
    private final PaymentProcessorFactory paymentProcessorFactory;
    private final PaymentAttemptService paymentAttemptService;
    private final PaymentStateMachineValidator stateMachineValidator;
    private final PaymentEventService paymentEventService;
    private final ObjectMapper objectMapper;
    private final PaymentFinancialService paymentFinancialService;

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request, String idempotencyKey) {
        UUID userId = SecurityUtil.getCurrentUserId();
        /*
         * IDEMPOTENCY VALIDATION
         */
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            String requestHash = RequestHashUtil.generateHash(request);
            PaymentIdempotency existingRecord = idempotencyService.validateOrGet(idempotencyKey, requestHash);
            if (existingRecord != null) {
                log.info("Idempotent replay detected key={}", idempotencyKey);
                Payment existingPayment = paymentRepository.findById(existingRecord.getPaymentId())
                        .orElseThrow(() -> new BusinessValidationException(ErrorCode.PAYMENT_NOT_FOUND));
                return paymentMapper.toResponse(existingPayment);
            }
        }
        /*
         * ORDER VALIDATION
         */
        InternalOrderValidationResponse order = orderValidationService.validateOrder(request.orderId(), request.amount(), request.currency().name());
        /*
         * PREVENT DUPLICATE PAYMENT
         */
        paymentRepository.findByOrderId(request.orderId()).ifPresent(payment -> {
            throw new BusinessValidationException(ErrorCode.DUPLICATE_PAYMENT_REQUEST);
        });
        /*
         * CALCULATE FINANCIAL SNAPSHOT
         */
        PaymentFinancialDetails financials = paymentFinancialService.calculateFinancials(order.merchantId(), request.amount());
        /*
         * CREATE PAYMENT
         */
        Payment payment = Payment.builder().paymentReference(ReferenceGeneratorUtil.generatePaymentReference()).orderId(request.orderId()).merchantId(order.merchantId()).userId(userId).amount(request.amount())
                .currency(Currency.valueOf(order.currency())).paymentMethod(request.paymentMethod()).description(request.description())
                /*
                 * Financial Snapshot
                 */.grossAmount(financials.grossAmount()).platformFeeAmount(financials.platformFeeAmount()).reserveAmount(financials.reserveAmount())
                .netSettlementAmount(financials.netSettlementAmount()).settlementDelayDays(financials.settlementDelayDays())
                .status(PaymentStatus.CREATED).build();
        paymentRepository.save(payment);
        paymentEventService.publishEvent(payment, PaymentEventType.PAYMENT_CREATED);
        log.info("Payment created paymentId={} orderId={}", payment.getId(), payment.getOrderId());
        /*
         * STATE TRANSITION
         */
        stateMachineValidator.validateTransition(payment.getStatus(), PaymentStatus.PROCESSING);
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
        paymentEventService.publishEvent(payment, PaymentEventType.PAYMENT_PROCESSING);
        /*
         * CREATE ATTEMPT
         */
        PaymentAttempt attempt = paymentAttemptService.createAttempt(payment);
        /*
         * PROCESS PAYMENT
         */
        PaymentProcessor processor = paymentProcessorFactory.getProcessor(payment.getPaymentMethod());
        long startTime = System.currentTimeMillis();
        PaymentGatewayResponse gatewayResponse = processor.process(payment, attempt);
        long processingTime = System.currentTimeMillis() - startTime;
        /*
         * SUCCESS FLOW
         */
        if (gatewayResponse.success()) {
            stateMachineValidator.validateTransition(payment.getStatus(), PaymentStatus.SUCCESS);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setGatewayReference(gatewayResponse.gatewayReference());
            payment.setFailureReason(null);
            paymentRepository.save(payment);
            paymentEventService.publishEvent(payment, PaymentEventType.PAYMENT_SUCCESS);
            paymentAttemptService.markAttemptSuccess(attempt.getId(), gatewayResponse, processingTime);
            orderValidationService.markOrderPaid(payment.getOrderId());
            log.info("Payment successful paymentId={} gatewayReference={}", payment.getId(), payment.getGatewayReference());
        } else {
            /*
             * FAILURE FLOW
             */
            stateMachineValidator.validateTransition(payment.getStatus(), PaymentStatus.FAILED);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(gatewayResponse.failureReason());
            paymentRepository.save(payment);
            paymentEventService.publishEvent(payment, PaymentEventType.PAYMENT_FAILED);
            paymentAttemptService.markAttemptFailed(attempt.getId(), gatewayResponse, processingTime);
            log.warn("Payment failed paymentId={} reason={}", payment.getId(), gatewayResponse.failureReason());
        }
        PaymentResponse response = paymentMapper.toResponse(payment);
        /*
         * SAVE IDEMPOTENCY RECORD
         */
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try {
                idempotencyService.saveRecord(idempotencyKey, RequestHashUtil.generateHash(request)
                        , payment.getId(), objectMapper.writeValueAsString(response));
            } catch (DataIntegrityViolationException ex) {
                log.warn("Concurrent idempotency conflict key={}", idempotencyKey);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String paymentReference) {
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.PAYMENT_NOT_FOUND));
        return paymentMapper.toResponse(payment);
    }
}