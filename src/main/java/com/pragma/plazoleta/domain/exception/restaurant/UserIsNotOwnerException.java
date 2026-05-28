package com.pragma.plazoleta.domain.exception.restaurant;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class UserIsNotOwnerException extends BusinessRuleException {

    private static final String ERROR_MESSAGE = "The provided user is not a valid restaurant owner.";

    public UserIsNotOwnerException() {
        super(ERROR_MESSAGE);
    }
}
