package com.payflowx.order.exception;

public class MerchantServiceUnavailableException
        extends RuntimeException {

    public MerchantServiceUnavailableException(String message) {
        super(message);
    }
}