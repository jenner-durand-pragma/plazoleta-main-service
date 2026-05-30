package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Dish;

public interface IDishServicePort {

    Dish createDish(Dish dish, Long ownerId);
    Dish updateDish(Long dishId, Integer price, String description, Long ownerId);
    Dish updateDishStatus(Long dishId, Boolean active, Long ownerId);
}
