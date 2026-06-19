package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class InvalidOrderStateException extends BusinessRuleException {

    public static final String ERROR_ORDER_NOT_PENDING_MESSAGE =
            "Order must be in PENDING state. Current state: %s";
    public static final String ERROR_ORDER_NOT_IN_PREPARATION_MESSAGE =
            "Order must be in IN_PREPARATION state. Current state: %s";

    public InvalidOrderStateException(String message) {
        super(message);
    }

    public static InvalidOrderStateException orderNotPending(String currentStatus) {
        return new InvalidOrderStateException(
          String.format(ERROR_ORDER_NOT_PENDING_MESSAGE, currentStatus)
        );
    }

    public static InvalidOrderStateException orderNotInPreparation(String currentStatus) {
        return new InvalidOrderStateException(
                String.format(ERROR_ORDER_NOT_IN_PREPARATION_MESSAGE, currentStatus)
        );
    }
}
