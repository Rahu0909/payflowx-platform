package com.payflowx.notification.service;

import com.payflowx.notification.dto.OrderNotificationMessage;

public interface OrderNotificationService {

    void process(OrderNotificationMessage message);
}
