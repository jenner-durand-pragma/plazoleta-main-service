package com.pragma.plazoleta.domain.exception.dish;

import com.pragma.plazoleta.domain.exception.NotFoundException;
import com.pragma.plazoleta.domain.model.Dish;

public class DishNotFoundException extends NotFoundException {

    private static final String ERROR_MESSAGE = "Dish not found.";

    public DishNotFoundException(Long dishId) {
        super(ERROR_MESSAGE, Dish.class.getSimpleName(), dishId);
    }
}
