package com.payflowx.order.exception;

import com.payflowx.order.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleBusinessException(BusinessValidationException ex) {
        log.error("Unhandled exception occurred", ex);
        return new ApiResponse<>(
                "FAILURE",
                ex.getErrorCode().name(),
                ex.getErrorCode().name(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleGeneric(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return new ApiResponse<>(
                "ERROR",
                "INTERNAL_ERROR",
                "Something went wrong",
                LocalDateTime.now()
        );
    }
}