package com.sebn.dashboard.exception;

/**
 * Raised when request parameters fail validation (HTTP 400).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
