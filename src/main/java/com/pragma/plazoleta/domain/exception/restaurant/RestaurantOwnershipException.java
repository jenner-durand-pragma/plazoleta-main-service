package com.pragma.plazoleta.domain.exception.restaurant;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class RestaurantOwnershipException extends BusinessRuleException {

    private static final String ERROR_MESSAGE = "Only the restaurant owner can manage this restaurant.";

    public RestaurantOwnershipException() {
        super(ERROR_MESSAGE);
    }
}
