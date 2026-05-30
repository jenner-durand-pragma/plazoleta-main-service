package com.pragma.plazoleta.domain.exception;

public class UnauthorizedException extends RuntimeException {

    protected UnauthorizedException(String message) {
        super(message);
    }

}
