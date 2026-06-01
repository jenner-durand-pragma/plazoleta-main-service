package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class OrderEmployeeOwnershipException extends BusinessRuleException {
    private static final String ERROR_MESSAGE = "This order does not belong to the employee's restaurant.";

    public OrderEmployeeOwnershipException() {
        super(ERROR_MESSAGE);
    }
}
