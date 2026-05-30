package com.pragma.plazoleta.domain.exception.restaurantemployee;

import com.pragma.plazoleta.domain.exception.ConflictException;

public class RemoteUserConflictException extends ConflictException {

    public RemoteUserConflictException(String message) {
        super(message);
    }

}
