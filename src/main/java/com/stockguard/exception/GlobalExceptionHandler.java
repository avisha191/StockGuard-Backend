
package com.stockguard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(
            IllegalArgumentException ex) {

        String message = ex.getMessage() != null
                ? ex.getMessage()
                : "Invalid request";

        HttpStatus status = message.toLowerCase().contains("stock")
                || message.toLowerCase().contains("already cancelled")
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(Map.of("error", message));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(
            ResponseStatusException ex) {

        String message = ex.getReason() != null
                ? ex.getReason()
                : "Request failed";

        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("error", message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");

        return ResponseEntity.badRequest()
                .body(Map.of("error", message));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException ex) {

        String message = ex.getMessage();

        if (message != null
                && message.toLowerCase().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", message));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error",
                        "An unexpected error occurred"
                ));
    }
}



