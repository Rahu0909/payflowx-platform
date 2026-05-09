package com.payflowx.order.serviceImpl;

import com.payflowx.order.constant.ErrorCode;
import com.payflowx.order.dto.response.InternalOrderValidationResponse;
import com.payflowx.order.entity.Order;
import com.payflowx.order.enums.OrderEventType;
import com.payflowx.order.enums.OrderStatus;
import com.payflowx.order.exception.BusinessValidationException;
import com.payflowx.order.repository.OrderRepository;
import com.payflowx.order.service.InternalOrderService;
import com.payflowx.order.service.OrderWebhookEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalOrderServiceImpl implements InternalOrderService {
    private final OrderRepository orderRepository;
    private final OrderWebhookEventService webhookEventService;

    @Override
    @Transactional(readOnly = true)
    public InternalOrderValidationResponse validateOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessValidationException(ErrorCode.ORDER_NOT_FOUND));
        /*
         * State Validation
         */
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessValidationException(ErrorCode.ORDER_CANCELLED);
        }
        if (order.getStatus() == OrderStatus.EXPIRED) {
            throw new BusinessValidationException(ErrorCode.ORDER_EXPIRED);
        }
        if (order.getStatus() == OrderStatus.PAID) {
            throw new BusinessValidationException(ErrorCode.ORDER_ALREADY_PAID);
        }
        /*
         * Expiry Validation
         */
        if (order.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessValidationException(ErrorCode.ORDER_EXPIRED);
        }
        return new InternalOrderValidationResponse(order.getId(), order.getMerchantId(), order.getAmount(), order.getCurrency().name(), order.getStatus().name(), true);
    }

    @Override
    @Transactional
    public void markOrderPaid(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessValidationException(ErrorCode.ORDER_NOT_FOUND));
        /*
         * Prevent duplicate payment marking
         */
        if (order.getStatus() == OrderStatus.PAID) {
            throw new BusinessValidationException(ErrorCode.ORDER_ALREADY_PAID);
        }
        /*
         * Prevent invalid transitions
         */
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessValidationException(ErrorCode.ORDER_CANCELLED);
        }
        if (order.getStatus() == OrderStatus.EXPIRED) {
            throw new BusinessValidationException(ErrorCode.ORDER_EXPIRED);
        }
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);
        webhookEventService.publishEvent(order, OrderEventType.ORDER_PAID);
        log.info("Order marked paid orderId={} amount={}", order.getId(), order.getAmount());
    }
}