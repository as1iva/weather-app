package org.as1iva.exception;

public class ApiRequestFailedException extends RuntimeException {
    public ApiRequestFailedException(String message) {
        super(message);
    }
}
