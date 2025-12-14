package com.petstarproject.petstar.exception;

public class VideoSourceRequiredException extends RuntimeException {
    public VideoSourceRequiredException(String message) {
        super(message);
    }

    public VideoSourceRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
