package com.payflowx.user.exception;

import com.payflowx.user.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    // Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse("FAILURE", null, request);
    }

    // Fallback (VERY IMPORTANT)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred", ex);
        return buildErrorResponse("INTERNAL_SERVER_ERROR", "Internal server error", request);
    }

    private ErrorResponse buildErrorResponse(String errorCode, String message, HttpServletRequest request) {
        return ErrorResponse.builder().timestamp(Instant.now().toString()).traceId(MDC.get("traceId") != null ? MDC.get("traceId") : "N/A").errorCode(errorCode).message(message).path(request.getRequestURI()).service(applicationName).build();
    }
}