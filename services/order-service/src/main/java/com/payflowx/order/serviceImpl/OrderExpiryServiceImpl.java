package com.payflowx.order.serviceImpl;

import com.payflowx.order.dto.event.OrderNotificationEvent;
import com.payflowx.order.entity.Order;
import com.payflowx.order.enums.OrderEventType;
import com.payflowx.order.enums.OrderStatus;
import com.payflowx.order.repository.OrderRepository;
import com.payflowx.order.service.OrderExpiryService;
import com.payflowx.order.service.OrderNotificationPublisher;
import com.payflowx.order.service.OrderWebhookEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExpiryServiceImpl implements OrderExpiryService {
    private final OrderRepository orderRepository;
    private final OrderWebhookEventService webhookEventService;
    private final OrderNotificationPublisher orderNotificationPublisher;

    @Override
    @Transactional
    public void expireOrders() {
        /*
         * Fetch expired CREATED orders
         */
        List<Order> expiredOrders = orderRepository.findByExpiresAtBeforeAndStatus(LocalDateTime.now(), OrderStatus.CREATED, PageRequest.of(0, 100)).getContent();
        if (expiredOrders.isEmpty()) {
            log.debug("No expired orders found");
            return;
        }
        expiredOrders.forEach(order -> {
            order.setStatus(OrderStatus.EXPIRED);
            webhookEventService.publishEvent(order, OrderEventType.ORDER_EXPIRED);
            publishOrderEvent(order, OrderEventType.ORDER_EXPIRED, "Order expired");
            log.info("Order expired orderId={} merchantId={}", order.getId(), order.getMerchantId());
        });
        orderRepository.saveAll(expiredOrders);
        log.info("Expired orders processed count={}", expiredOrders.size());
    }

    private void publishOrderEvent(Order order, OrderEventType eventType, String message) {
        OrderNotificationEvent event = OrderNotificationEvent.builder()
                .eventId(UUID.randomUUID())
                .orderId(order.getId())
                .merchantId(order.getMerchantId())
                .merchantBusinessName(order.getMerchantBusinessName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .eventType(eventType.name())
                .message(message)
                .amount(order.getAmount())
                .currency(order.getCurrency().name())
                .occurredAt(LocalDateTime.now())
                .build();
        orderNotificationPublisher.publish(event);
    }
}