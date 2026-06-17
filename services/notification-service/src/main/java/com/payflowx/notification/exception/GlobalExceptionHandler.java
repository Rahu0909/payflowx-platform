package com.payflowx.notification.exception;

import com.payflowx.notification.dto.ApiResponse;
import com.payflowx.notification.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;

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
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return buildErrorResponse("FAILED", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred", ex);
        return buildErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage(), request);
    }

    private ErrorResponse buildErrorResponse(String errorCode, String message, HttpServletRequest request) {
        return ErrorResponse.builder().timestamp(Instant.now().toString()).traceId(MDC.get("traceId") != null ? MDC.get("traceId") : "N/A").errorCode(errorCode).message(message).path(request.getRequestURI()).service(applicationName).build();
    }
}