package com.payflowx.order.serviceImpl;

import com.payflowx.order.constant.ErrorCode;
import com.payflowx.order.dto.request.CreateOrderRequest;
import com.payflowx.order.dto.response.InternalMerchantValidationResponse;
import com.payflowx.order.dto.response.OrderResponse;
import com.payflowx.order.entity.Order;
import com.payflowx.order.enums.OrderEventType;
import com.payflowx.order.enums.OrderStatus;
import com.payflowx.order.exception.BusinessValidationException;
import com.payflowx.order.mapper.OrderMapper;
import com.payflowx.order.repository.OrderRepository;
import com.payflowx.order.service.IdempotencyService;
import com.payflowx.order.service.MerchantValidationService;
import com.payflowx.order.service.OrderService;
import com.payflowx.order.service.OrderWebhookEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final MerchantValidationService merchantValidationService;
    private final OrderMapper orderMapper;
    private final IdempotencyService idempotencyService;
    private final OrderWebhookEventService webhookEventService;

    @Override
    @Transactional
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request, String idempotencyKey) {
        /*
         * IDEMPOTENCY CHECK
         */
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existingRecord = idempotencyService.validateOrGet(idempotencyKey);
            if (existingRecord != null) {
                log.info("Idempotent replay detected key={}", idempotencyKey);
                Order existingOrder = orderRepository.findById(existingRecord.getResourceId()).orElseThrow();
                return orderMapper.toResponse(existingOrder);
            }
        }
        /*
         * EXISTING ORDER CREATION LOGIC
         */
        InternalMerchantValidationResponse merchantValidation = merchantValidationService.validateMerchant(userId);
        if (orderRepository.findByReceipt(request.receipt()).isPresent()) {
            throw new BusinessValidationException(ErrorCode.DUPLICATE_RECEIPT);
        }
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        Order order = Order.builder().merchantId(UUID.fromString(merchantValidation.merchantId())).merchantBusinessName(merchantValidation.businessName()).amount(request.amount()).currency(request.currency()).receipt(request.receipt()).customerEmail(request.customerEmail()).customerPhone(request.customerPhone()).status(OrderStatus.CREATED).expiresAt(expiresAt).build();
        orderRepository.save(order);
        webhookEventService.publishEvent(order, OrderEventType.ORDER_CREATED);
        /*
         * SAVE IDEMPOTENCY RECORD
         */
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyService.saveRecord(idempotencyKey, order.getId(), "ORDER");
        }
        log.info("Order created orderId={} merchantId={} amount={}", order.getId(), order.getMerchantId(), order.getAmount());
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getOrder(UUID userId, UUID orderId) {
        InternalMerchantValidationResponse merchantValidation = merchantValidationService.validateMerchant(userId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessValidationException(ErrorCode.ORDER_NOT_FOUND));
        /*
         * Ownership Validation
         */
        if (!order.getMerchantId().equals(UUID.fromString(merchantValidation.merchantId()))) {
            throw new BusinessValidationException(ErrorCode.ORDER_NOT_FOUND);
        }
        return orderMapper.toResponse(order);
    }

    @Override
    public Page<OrderResponse> getMyOrders(UUID userId, Pageable pageable) {
        InternalMerchantValidationResponse merchantValidation = merchantValidationService.validateMerchant(userId);
        return orderRepository.findByMerchantId(UUID.fromString(merchantValidation.merchantId()), pageable).map(orderMapper::toResponse);
    }

    @Override
    public void cancelOrder(UUID userId, UUID orderId) {
        InternalMerchantValidationResponse merchantValidation = merchantValidationService.validateMerchant(userId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessValidationException(ErrorCode.ORDER_NOT_FOUND));
        /*
         * Ownership Validation
         */
        if (!order.getMerchantId().equals(UUID.fromString(merchantValidation.merchantId()))) {
            throw new BusinessValidationException(ErrorCode.ORDER_NOT_FOUND);
        }
        /*
         * State Validation
         */
        if (order.getStatus() == OrderStatus.PAID) {
            throw new BusinessValidationException(ErrorCode.ORDER_ALREADY_PAID);
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessValidationException(ErrorCode.ORDER_ALREADY_CANCELLED);
        }
        if (order.getStatus() == OrderStatus.EXPIRED) {
            throw new BusinessValidationException(ErrorCode.ORDER_EXPIRED);
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        webhookEventService.publishEvent(order, OrderEventType.ORDER_CANCELLED);
        log.info("Order cancelled orderId={}", order.getId());
    }
}