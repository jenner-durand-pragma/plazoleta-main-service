package com.pragma.plazoleta.domain.exception.restaurant;

import com.pragma.plazoleta.domain.exception.ConflictException;

public class NitAlreadyExistsException extends ConflictException {

    private static final String ERROR_MESSAGE = "A restaurant with this NIT already exists.";
    private static final String ERROR_FIELD = "nit";

    public NitAlreadyExistsException() {
        super(ERROR_MESSAGE, ERROR_FIELD);
    }
}
