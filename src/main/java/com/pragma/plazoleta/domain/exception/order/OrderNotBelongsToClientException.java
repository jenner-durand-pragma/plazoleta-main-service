package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.exception.NotFoundException;
import com.pragma.plazoleta.domain.model.Order;

public class OrderNotBelongsToClientException extends NotFoundException {

    private static final String ERROR_MESSAGE = "Order not found.";

    public OrderNotBelongsToClientException(Long orderId) {
      super(ERROR_MESSAGE, Order.class.getSimpleName(), orderId);
    }
}
