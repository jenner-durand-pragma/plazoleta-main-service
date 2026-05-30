package com.pragma.plazoleta.infrastructure.configuration.security.token.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
