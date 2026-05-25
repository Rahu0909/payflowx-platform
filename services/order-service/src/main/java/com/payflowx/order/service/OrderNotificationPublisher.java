package com.payflowx.order.service;

import com.payflowx.order.dto.event.OrderNotificationEvent;

public interface OrderNotificationPublisher {

    void publish(OrderNotificationEvent event);
}
