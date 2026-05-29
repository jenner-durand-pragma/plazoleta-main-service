package com.pragma.plazoleta.domain.exception.dish;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class DishOwnershipException extends BusinessRuleException {

    private static final String ERROR_MESSAGE = "Only the restaurant owner can manage dishes in this restaurant.";

    public DishOwnershipException() {
        super(ERROR_MESSAGE);
    }
}
