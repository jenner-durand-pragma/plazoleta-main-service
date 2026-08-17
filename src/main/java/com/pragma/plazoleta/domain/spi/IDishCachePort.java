package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Dish;

import java.util.Optional;

public interface IDishCachePort {

    Optional<Dish> getDishById(Long id);
    void saveDish(Dish dish);

    Optional<PagedResult<Dish>> getDishList(Long restaurantId, Long categoryId, Integer page, Integer size);
    void saveDishList(Long restaurantId, Long categoryId, Integer page, Integer size, PagedResult<Dish> list);

    void deleteDishPagesCached();
}
