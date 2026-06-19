package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class OrderChefOwnershipException extends BusinessRuleException {

    private static final String ERROR_MESSAGE = "The employee is not the assigned chef for this order";

    public OrderChefOwnershipException() {
        super(ERROR_MESSAGE);
    }
}
