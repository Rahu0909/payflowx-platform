package com.payflowx.order.exception;


import com.payflowx.order.constant.ErrorCode;

public class BusinessValidationException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessValidationException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}