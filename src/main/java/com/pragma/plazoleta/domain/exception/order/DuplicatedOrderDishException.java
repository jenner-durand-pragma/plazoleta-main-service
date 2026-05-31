package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class DuplicatedOrderDishException extends BusinessRuleException {

    private static final String ERROR_MESSAGE = "Duplicate dish ids in order items are not allowed.";

    public DuplicatedOrderDishException() {
        super(ERROR_MESSAGE);
    }
}
