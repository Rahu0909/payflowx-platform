package com.payflowx.merchant.exception;

public class RabbitMqPublisherException extends RuntimeException {

    public RabbitMqPublisherException(String message) {
        super(message);
    }

    public RabbitMqPublisherException(String message, Throwable cause) {
        super(message, cause);
    }
}