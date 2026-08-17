package com.pragma.plazoleta.domain.service;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Dish;

public interface IDishCacheService {

    Dish getDishById(Long id);
    Dish saveDish(Dish dish);
    PagedResult<Dish> listDishes(Long restaurantId, Long categoryId, Integer page, Integer size);
}
