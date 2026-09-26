package org.fadhel.jisrnihongoplatform.advice;


import org.fadhel.jisrnihongoplatform.dto.ApiResponse;
import org.fadhel.jisrnihongoplatform.exception.ApiException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    // Handles custom business logic errors thrown from service classes
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse> handleApiException(ApiException e) {
        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }

    // Handles validation errors triggered by @Valid annotations
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getFieldError() != null ? e.getFieldError().getDefaultMessage() : "Validation error";
        return ResponseEntity.status(400).body(new ApiResponse(message));
    }

    // Handles database-level constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ResponseEntity.status(400).body(new ApiResponse("Data integrity error: unique constraint or key rule violated"));
    }
}
