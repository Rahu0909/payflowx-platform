package com.payflowx.notification.constant;

public final class WebhookRetryConstants {

    private WebhookRetryConstants() {
    }

    public static final int MAX_RETRY_COUNT = 5;

    public static final long INITIAL_RETRY_DELAY_MINUTES = 1;

    public static final long MAX_RETRY_DELAY_MINUTES = 60;
}