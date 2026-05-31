package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Dish;

public interface IDishServicePort {

    Dish createDish(Long restaurantId, Dish dish, Long ownerId);
    Dish updateDish(Long dishId, Integer price, String description, Long ownerId);
    Dish updateDishStatus(Long dishId, Boolean active, Long ownerId);

    PagedResult<Dish> listDishesByRestaurant(
            Long restaurantId,
            Long categoryId,
            Integer page,
            Integer size
    );
}
