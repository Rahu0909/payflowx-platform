package com.payflowx.notification.config;

import com.payflowx.notification.exception.ExternalServiceException;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MerchantFeignErrorDecoder {
    @Bean
    public ErrorDecoder merchantErrorDecoder() {
        return (methodKey, response) -> {
            if (response.status() >= 500) {
                return new ExternalServiceException("Merchant Service unavailable");
            }
            if (response.status() == 404) {
                return new ExternalServiceException("Merchant webhook not found");
            }
            return new ErrorDecoder.Default().decode(methodKey, response);
        };
    }
}