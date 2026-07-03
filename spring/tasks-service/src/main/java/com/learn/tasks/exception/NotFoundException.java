package com.learn.tasks.exception;

// Thrown by the service when an id doesn't exist -> mapped to HTTP 404 by the advice (≈ Nest NotFoundException).
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
