package com.payflowx.auth.exception;

import com.payflowx.auth.constant.AppConstants;
import com.payflowx.auth.constant.AppMessages;
import com.payflowx.auth.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
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
