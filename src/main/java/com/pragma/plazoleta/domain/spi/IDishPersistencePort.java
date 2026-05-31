package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Dish;

import java.util.List;

public interface IDishPersistencePort {

    Dish save(Dish dish);
    Dish findById(Long id);

    List<Dish> findAllByIdIn(List<Long> ids);
    PagedResult<Dish> findActiveByRestaurantAndCategoryPaginated(
            Long restaurantId,
            Long categoryId,
            Integer page,
            Integer size
    );
}
