package com.example.gato.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        var first = ex.getBindingResult().getFieldErrors().stream().findFirst();
        String msg = first.map(f -> f.getField() + " " + f.getDefaultMessage()).orElse("Validation error");
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<?> handleERE(ErrorResponseException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        return ResponseEntity.status(status).body(Map.of("error", ex.getBody().getDetail()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOther(Exception ex) {
        Logger.getLogger(GlobalExceptionHandler.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        return ResponseEntity.status(500).body(Map.of("error", "Unexpected error"));
    }
}
