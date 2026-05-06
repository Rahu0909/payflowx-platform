package com.payflowx.merchant.exception;

import com.payflowx.merchant.constant.ErrorCode;

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