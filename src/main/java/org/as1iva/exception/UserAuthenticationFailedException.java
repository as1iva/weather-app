package org.as1iva.exception;

public class UserAuthenticationFailedException extends RuntimeException {
    public UserAuthenticationFailedException(String message) {
        super(message);
    }
}
