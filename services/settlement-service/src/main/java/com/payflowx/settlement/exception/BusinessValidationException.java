package com.payflowx.settlement.exception;

import com.payflowx.settlement.constant.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessValidationException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessValidationException(ErrorCode errorCode) {

        super(errorCode.name());

        this.errorCode = errorCode;
    }
}