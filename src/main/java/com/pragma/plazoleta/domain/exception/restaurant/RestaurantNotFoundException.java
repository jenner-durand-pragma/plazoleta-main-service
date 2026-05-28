package com.pragma.plazoleta.domain.exception.restaurant;

import com.pragma.plazoleta.domain.exception.NotFoundException;
import com.pragma.plazoleta.domain.model.Restaurant;

public class RestaurantNotFoundException extends NotFoundException {

    private static final String ERROR_MESSAGE = "Restaurant not found.";

    public RestaurantNotFoundException(Long restaurantId) {
        super(ERROR_MESSAGE, Restaurant.class.getSimpleName(), restaurantId);
    }
}
