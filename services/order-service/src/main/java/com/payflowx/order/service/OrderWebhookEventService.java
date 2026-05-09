package com.payflowx.order.service;

import com.payflowx.order.entity.Order;
import com.payflowx.order.enums.OrderEventType;

public interface OrderWebhookEventService {

    void publishEvent(Order order, OrderEventType eventType);
}