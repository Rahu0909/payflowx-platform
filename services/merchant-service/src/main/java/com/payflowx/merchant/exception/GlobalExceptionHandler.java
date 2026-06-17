package com.payflowx.merchant.exception;

import com.payflowx.merchant.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Value("${spring.application.name}")
    private String applicationName;

    @ExceptionHandler(BusinessValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusinessException(BusinessValidationException ex, HttpServletRequest request) {
        log.warn("Business validation failed error={}", ex.getErrorCode());
        return buildErrorResponse(ex.getErrorCode().name(), ex.getMessage(), request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(Exception ex, HttpServletRequest request) {
        log.warn("Validation failed message={}", ex.getMessage());
        return buildErrorResponse("INVALID_REQUEST", ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation occurred");
        return buildErrorResponse("DATA_INTEGRITY_VIOLATION", ex.getMessage(), request);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleOptimisticLocking(ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        log.warn("Optimistic locking conflict occurred");
        return buildErrorResponse("CONCURRENT_MODIFICATION_DETECTED", ex.getMessage(), request);
    }

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