package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Dish;

public interface IDishPersistencePort {

    Dish save(Dish dish);
    Dish findById(Long id);

    PagedResult<Dish> findActiveByRestaurantAndCategoryPaginated(
            Long restaurantId,
            Long categoryId,
            Integer page,
            Integer size
    );
}
