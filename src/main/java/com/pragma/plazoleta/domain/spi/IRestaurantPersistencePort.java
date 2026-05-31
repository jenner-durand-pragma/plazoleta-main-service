package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;

public interface IRestaurantPersistencePort {

    Restaurant save(Restaurant restaurant);

    Restaurant findById(Long id);
    Boolean existsByNit(String nit);

    PagedResult<Restaurant> findAllPaginatedByNameAsc(Integer page, Integer size);
}
