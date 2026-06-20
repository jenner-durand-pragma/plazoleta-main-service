package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class InvalidOrderStateException extends BusinessRuleException {

    public static final String ERROR_ORDER_INVALID_STATUS = "Order must be in %s state. Current state: %s";

    public InvalidOrderStateException(String message) {
        super(message);
    }

    public static InvalidOrderStateException orderNotPending(String currentStatus) {
        return new InvalidOrderStateException(
          String.format(ERROR_ORDER_INVALID_STATUS, OrderStatus.PENDING.name(), currentStatus)
        );
    }

    public static InvalidOrderStateException orderNotInPreparation(String currentStatus) {
        return new InvalidOrderStateException(
                String.format(ERROR_ORDER_INVALID_STATUS, OrderStatus.IN_PREPARATION.name(), currentStatus)
        );
    }

    public static InvalidOrderStateException orderNotReady(String currentStatus) {
        return new InvalidOrderStateException(
                String.format(ERROR_ORDER_INVALID_STATUS, OrderStatus.READY.name(), currentStatus)
        );
    }
}
