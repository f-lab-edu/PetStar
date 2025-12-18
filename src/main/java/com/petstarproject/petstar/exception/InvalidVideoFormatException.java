package com.petstarproject.petstar.exception;

public class InvalidVideoFormatException extends RuntimeException {
    public InvalidVideoFormatException(String message) {
        super(message);
    }
}
