package com.payflowx.notification.service;

import com.payflowx.notification.entity.NotificationEvent;

public interface WebhookDispatcherService {

    void dispatchWebhook(NotificationEvent notificationEvent);
}