    package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class OrderItemsEmptyException extends BusinessRuleException {

    private static final String ERROR_MESSAGE = "An order must contain at least one item.";

    public OrderItemsEmptyException() {
        super(ERROR_MESSAGE);
    }
}
