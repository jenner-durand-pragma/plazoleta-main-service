package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class OrderCannotBeCancelledException extends BusinessRuleException {

    public static final String ERROR_MESSAGE = "Sorry, your order is already in preparation and cannot be cancelled.";

    public OrderCannotBeCancelledException() {
        super(ERROR_MESSAGE);
    }
}
