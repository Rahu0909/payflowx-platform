package com.payflowx.user.exception;

import com.payflowx.user.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Business Validation (your core errors)
    @ExceptionHandler(BusinessValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleBusinessException(BusinessValidationException ex) {
        return new ApiResponse<>(
                "FAILURE",
                ex.getErrorCode().name(),
                ex.getMessage(),
                LocalDateTime.now()
        );
    }
    // Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNotFound(ResourceNotFoundException ex) {
        return new ApiResponse<>(
                "FAILURE",
                null,
                ex.getMessage(),
                java.time.LocalDateTime.now()
        );
    }

    // Fallback (VERY IMPORTANT)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneric(Exception ex) {
        return new ApiResponse<>(
                "ERROR",
                null,
                "Something went wrong",
                java.time.LocalDateTime.now()
        );
    }
}