package com.payflowx.merchant.exception;

import com.payflowx.merchant.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleBusinessException(BusinessValidationException ex) {
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
        return new ApiResponse<>(
                "ERROR",
                "INTERNAL_ERROR",
                "Something went wrong",
                LocalDateTime.now()
        );
    }
}