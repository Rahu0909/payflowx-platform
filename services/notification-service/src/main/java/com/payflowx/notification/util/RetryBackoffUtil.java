package com.payflowx.notification.util;

import com.payflowx.notification.constant.WebhookRetryConstants;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RetryBackoffUtil {

    public static long calculateDelayMinutes(int retryCount) {

        long delay = (long) Math.pow(2, retryCount);

        return Math.min(delay, WebhookRetryConstants.MAX_RETRY_DELAY_MINUTES);
    }
}