package com.payflowx.auth.exception;

import com.payflowx.auth.constant.AppConstants;
import com.payflowx.auth.constant.AppMessages;
import com.payflowx.auth.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.error("Business exception occurred", ex);
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        AppConstants.ERROR,
                        ex.getMessage(),
                        List.of(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidationException(BusinessValidationException ex) {
        log.error("Business Validation exception occurred", ex);
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        AppConstants.ERROR,
                        ex.getMessage(),
                        List.of(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        log.error("Validation exception occurred", ex);
        var errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(err -> ((FieldError) err).getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        AppConstants.ERROR,
                        AppMessages.VALIDATION_FAILED,
                        errors,
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(
                        AppConstants.ERROR,
                        AppMessages.INTERNAL_ERROR,
                        List.of(ex.getMessage()),
                        LocalDateTime.now()
                )
        );
    }
}