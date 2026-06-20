package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.exception.ConflictException;

public class InvalidSecurityPinException extends ConflictException {

    private static final String ERROR_MESSAGE = "The provided security PIN is invalid.";

    public InvalidSecurityPinException() {
        super(ERROR_MESSAGE);
    }
}
