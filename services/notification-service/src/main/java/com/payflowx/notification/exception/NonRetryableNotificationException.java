package com.payflowx.notification.exception;

public class NonRetryableNotificationException extends RuntimeException {

    public NonRetryableNotificationException(String message) {
        super(message);
    }
}