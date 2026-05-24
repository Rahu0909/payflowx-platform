package com.payflowx.notification.service;

import com.payflowx.notification.dto.AuthNotificationMessage;

public interface AuthNotificationService {

    void process(AuthNotificationMessage message);
}