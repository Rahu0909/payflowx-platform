package com.payflowx.user.exception;

import com.payflowx.user.constant.ErrorCode;

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