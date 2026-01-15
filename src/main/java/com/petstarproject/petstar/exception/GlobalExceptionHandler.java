package com.petstarproject.petstar.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
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
        return ResponseEntity.badRequest().body(new ErrorResponse("잘못된 요청 형식입니다.", HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(VideoSourceRequiredException.class)
    public ResponseEntity<ErrorResponse> handelVideoSourceRequiredException(VideoSourceRequiredException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("동영상 파일은 필수입니다.", HttpStatus.BAD_REQUEST.value()));
    }
// todo: SpringSecurity 도입후 코드 재사용
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                .body(new ErrorResponse("접근 권한이 없습니다.", HttpStatus.FORBIDDEN.value()));
//    }

    @ExceptionHandler(InvalidVideoFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVideoFormat(InvalidVideoFormatException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())
        );
    }

    @ExceptionHandler(VideoDurationExtractFailedException.class)
    public ResponseEntity<ErrorResponse> handleDurationExtractFailed(VideoDurationExtractFailedException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponse("동영상 길이 추출에 실패했습니다.", HttpStatus.UNPROCESSABLE_ENTITY.value())
        );
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicatedEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(e.getMessage(), HttpStatus.CONTINUE.value())
        );
    }

    public record ErrorResponse(String message, int status) {
    }
}
