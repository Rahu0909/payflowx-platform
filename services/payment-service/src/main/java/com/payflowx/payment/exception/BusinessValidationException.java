package com.payflowx.payment.exception;

import com.payflowx.payment.constant.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessValidationException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessValidationException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }
}