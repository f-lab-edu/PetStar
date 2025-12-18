package com.petstarproject.petstar.exception;

public class VideoDurationExtractFailedException extends RuntimeException {
    public VideoDurationExtractFailedException(String message) {
      super(message);
    }

    public VideoDurationExtractFailedException(String message, Throwable cause) {
      super(message, cause);
    }
}
