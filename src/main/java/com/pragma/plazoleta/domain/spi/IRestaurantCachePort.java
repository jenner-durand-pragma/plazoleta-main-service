package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;

import java.util.Optional;

public interface IRestaurantCachePort {
    Optional<Restaurant> getRestaurantById(Long id);
    void saveRestaurant(Restaurant restaurant);

    Optional<PagedResult<Restaurant>> getRestaurantList(Integer page, Integer size);
    void saveRestaurantList(Integer page, Integer size, PagedResult<Restaurant> restaurants);

    void deleteRestaurantPagesCached();
}
