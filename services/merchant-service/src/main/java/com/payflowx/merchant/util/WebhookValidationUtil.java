package com.payflowx.merchant.util;

import com.payflowx.merchant.constant.ErrorCode;
import com.payflowx.merchant.exception.BusinessValidationException;

import java.net.URI;

public final class WebhookValidationUtil {

    private WebhookValidationUtil() {
    }
    public static void validateWebhookUrl(String url) {
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equals("https") || scheme.equals("http"))) {
                throw new BusinessValidationException(ErrorCode.INVALID_WEBHOOK_URL);
            }
        } catch (Exception ex) {
            throw new BusinessValidationException(ErrorCode.INVALID_WEBHOOK_URL);
        }
    }
}