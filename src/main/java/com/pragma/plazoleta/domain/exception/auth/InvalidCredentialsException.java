package com.pragma.plazoleta.domain.exception.auth;

import com.pragma.plazoleta.domain.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {

    private static final String ERROR_MESSAGE = "Invalid email or password.";

    public InvalidCredentialsException() {
        super(ERROR_MESSAGE);
    }
}
