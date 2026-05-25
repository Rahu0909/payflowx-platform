package com.payflowx.user.service;

import com.payflowx.user.dto.event.UserNotificationEvent;

public interface UserNotificationPublisher {

    void publish(UserNotificationEvent event);
}
