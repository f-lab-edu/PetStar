package com.petstarproject.petstar.exception;

public class JsonConverException extends RuntimeException {
    public JsonConverException(String message) {
        super(message);
    }

    public JsonConverException(String message, Throwable cause) {
        super(message, cause);
    }
}
