package com.payflowx.notification.service;

import com.payflowx.notification.dto.UserNotificationMessage;

public interface UserNotificationService {

    void process(UserNotificationMessage message);
}
