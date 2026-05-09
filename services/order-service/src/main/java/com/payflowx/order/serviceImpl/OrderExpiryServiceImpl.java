package com.payflowx.order.serviceImpl;

import com.payflowx.order.entity.Order;
import com.payflowx.order.enums.OrderEventType;
import com.payflowx.order.enums.OrderStatus;
import com.payflowx.order.repository.OrderRepository;
import com.payflowx.order.service.OrderExpiryService;
import com.payflowx.order.service.OrderWebhookEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExpiryServiceImpl implements OrderExpiryService {
    private final OrderRepository orderRepository;
    private final OrderWebhookEventService webhookEventService;

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
            log.info("Order expired orderId={} merchantId={}", order.getId(), order.getMerchantId());
        });
        orderRepository.saveAll(expiredOrders);
        log.info("Expired orders processed count={}", expiredOrders.size());
    }
}