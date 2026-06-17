package com.payflowx.payment.exception;

import com.payflowx.payment.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.application.name}")
    private String applicationName;

    @ExceptionHandler(BusinessValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusinessException(BusinessValidationException ex, HttpServletRequest request) {
        log.warn("Business validation failed error={}", ex.getErrorCode());
        return buildErrorResponse(ex.getErrorCode().name(), ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream().findFirst().map(error -> error.getField() + " " + error.getDefaultMessage()).orElse("Validation failed");
        return buildErrorResponse("VALIDATION_ERROR", message, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        return buildErrorResponse("CONSTRAINT_VIOLATION", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred", ex);
        return buildErrorResponse("INTERNAL_SERVER_ERROR", "Internal server error", request);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleOptimisticLocking(ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        return buildErrorResponse("CONCURRENT_MODIFICATION", "Concurrent modification detected. Please retry.", request);
    }

    private ErrorResponse buildErrorResponse(String errorCode, String message, HttpServletRequest request) {
        return ErrorResponse.builder().timestamp(Instant.now().toString()).traceId(MDC.get("traceId") != null ? MDC.get("traceId") : "N/A").errorCode(errorCode).message(message).path(request.getRequestURI()).service(applicationName).build();
    }
}
