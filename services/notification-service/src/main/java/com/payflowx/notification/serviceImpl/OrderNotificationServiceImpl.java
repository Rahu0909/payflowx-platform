package com.payflowx.notification.serviceImpl;

import com.payflowx.notification.dto.OrderNotificationMessage;
import com.payflowx.notification.service.OrderNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNotificationServiceImpl implements OrderNotificationService {
    private final NotificationMetricsService notificationMetricsService;
    @Override
    public void process(OrderNotificationMessage message) {
        try {
            log.info("Processing order event type={} orderId={}",
                    message.eventType(),
                    message.orderId());
            switch (message.eventType()) {
                case "ORDER_CREATED" -> handleOrderCreated(message);
                case "ORDER_CANCELLED" -> handleOrderCancelled(message);
                case "ORDER_PAID" -> handleOrderPaid(message);
                case "ORDER_EXPIRED" -> handleOrderExpired(message);
                default -> log.warn("Unsupported order eventType={}", message.eventType());
            }
            notificationMetricsService.incrementSent();
            notificationMetricsService.incrementEmail();
        } catch (Exception ex) {
            notificationMetricsService.incrementFailed();
            throw ex;
        }
    }

    private void handleOrderCreated(OrderNotificationMessage message) {
        log.info("Handle order created notification orderId={}", message.orderId());
    }

    private void handleOrderCancelled(OrderNotificationMessage message) {
        log.info("Handle order cancelled notification orderId={}", message.orderId());
    }

    private void handleOrderPaid(OrderNotificationMessage message) {
        log.info("Handle order paid notification orderId={}", message.orderId());
    }

    private void handleOrderExpired(OrderNotificationMessage message) {
        log.info("Handle order expired notification orderId={}", message.orderId());
    }
}