package com.petstarproject.petstar.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(JsonConvertException.class)
    public ResponseEntity<ErrorResponse> handleJsonConvertException(JsonConvertException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("잘못된 요청 형식입니다.", 400));
    }

    public record ErrorResponse(String message, int status) {
    }
}
