package com.pragma.plazoleta.domain.exception.restaurantemployee;

import com.pragma.plazoleta.domain.exception.ConflictException;

public class UserInformationConflictException extends ConflictException {

    public UserInformationConflictException(String message) {
        super(message);
    }

}
