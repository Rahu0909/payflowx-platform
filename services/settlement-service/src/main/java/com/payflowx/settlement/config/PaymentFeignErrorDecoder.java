package com.payflowx.settlement.config;

import com.payflowx.settlement.exception.ExternalServiceException;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentFeignErrorDecoder {
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() >= 500) {
                return new ExternalServiceException("Payment Service unavailable");
            }
            if (response.status() == 404) {
                return new ExternalServiceException("Payment not found");
            }
            return new ErrorDecoder.Default().decode(methodKey, response);
        };
    }
}