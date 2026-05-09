package com.payflowx.order.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflowx.order.entity.Order;
import com.payflowx.order.entity.OrderWebhookEvent;
import com.payflowx.order.enums.OrderEventType;
import com.payflowx.order.repository.OrderWebhookEventRepository;
import com.payflowx.order.service.OrderWebhookEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderWebhookEventServiceImpl implements OrderWebhookEventService {
    private final OrderWebhookEventRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public void publishEvent(Order order, OrderEventType eventType) {
        try {
            Map<String, Object> payload = Map.of("orderId", order.getId(), "merchantId", order.getMerchantId(), "amount", order.getAmount(), "currency", order.getCurrency(), "status", order.getStatus(), "eventType", eventType.name());
            OrderWebhookEvent event = OrderWebhookEvent.builder().orderId(order.getId()).merchantId(order.getMerchantId()).eventType(eventType).payload(objectMapper.writeValueAsString(payload)).build();
            repository.save(event);
            log.info("Webhook event published orderId={} type={}", order.getId(), eventType);
        } catch (Exception ex) {
            log.error("Webhook publish failed orderId={}", order.getId(), ex);
        }
    }
}