package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.exception.ConflictException;

public class ClientHasActiveOrderException extends ConflictException {

    private static final String ERROR_MESSAGE = "The client already has an active order.";

    public ClientHasActiveOrderException() {
        super(ERROR_MESSAGE);
    }
}
