package com.payflowx.notification.exception;

import com.payflowx.notification.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessValidationException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessValidationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}