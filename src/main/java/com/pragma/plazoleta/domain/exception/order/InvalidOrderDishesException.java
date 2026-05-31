package com.pragma.plazoleta.domain.exception.order;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class InvalidOrderDishesException extends BusinessRuleException {

    public static final String ERROR_DISHES_NOT_FROM_RESTAURANT_MESSAGE =
            "All dishes must belong to the order's restaurant.";
    public static final String ERROR_DISHES_NOT_ACTIVE_MESSAGE =
            "All dishes must be active.";
    public static final String ERROR_DISHES_NOT_FOUND_MESSAGE =
            "One or more dishes in the order were not found.";

    public InvalidOrderDishesException(String message) {
        super(message);
    }

    public static InvalidOrderDishesException notFromRestaurant() {
        return new InvalidOrderDishesException(ERROR_DISHES_NOT_FROM_RESTAURANT_MESSAGE);
    }

    public static InvalidOrderDishesException notActive() {
        return new InvalidOrderDishesException(ERROR_DISHES_NOT_ACTIVE_MESSAGE);
    }

    public static InvalidOrderDishesException notFound() {
        return new InvalidOrderDishesException(ERROR_DISHES_NOT_FOUND_MESSAGE);
    }
}
