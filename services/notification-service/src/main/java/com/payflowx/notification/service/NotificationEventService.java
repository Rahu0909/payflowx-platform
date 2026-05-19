package com.payflowx.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payflowx.notification.dto.MerchantNotificationMessage;
import com.payflowx.notification.dto.PaymentNotificationMessage;
import com.payflowx.notification.dto.TreasuryNotificationMessage;

public interface NotificationEventService {

    void processPaymentNotification(PaymentNotificationMessage message) throws JsonProcessingException;

    void processTreasuryNotification(TreasuryNotificationMessage message) throws JsonProcessingException;

    void processMerchantNotification(MerchantNotificationMessage message) throws JsonProcessingException;
}