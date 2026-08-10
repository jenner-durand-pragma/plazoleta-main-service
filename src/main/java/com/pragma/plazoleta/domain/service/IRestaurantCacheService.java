package com.pragma.plazoleta.domain.service;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;

public interface IRestaurantCacheService {

    Restaurant getRestaurantById(Long id);
    Restaurant saveRestaurant(Restaurant restaurant);
    PagedResult<Restaurant> listRestaurants(Integer page, Integer size);
}
