    package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class OrderDishesEmptyException extends BusinessRuleException {

    private static final String ERROR_MESSAGE = "An order must contain at least one item.";

    public OrderDishesEmptyException() {
        super(ERROR_MESSAGE);
    }
}
