package com.payflowx.payment.strategy;

import com.payflowx.payment.constant.ErrorCode;
import com.payflowx.payment.enums.PaymentMethod;
import com.payflowx.payment.exception.BusinessValidationException;
import com.payflowx.payment.processor.PaymentProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentProcessorFactory {

    private final List<PaymentProcessor> processors;

    public PaymentProcessorFactory(List<PaymentProcessor> processors) {
        this.processors = processors;
    }

    public PaymentProcessor getProcessor(PaymentMethod paymentMethod) {
        return processors.stream().filter(processor -> processor.supportedMethod() == paymentMethod)
                .findFirst()
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.PAYMENT_METHOD_NOT_SUPPORTED));
    }
}