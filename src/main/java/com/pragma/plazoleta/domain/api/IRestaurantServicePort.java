package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;

public interface IRestaurantServicePort {

    Restaurant createRestaurant(Restaurant restaurant);
    PagedResult<Restaurant> listRestaurants(Integer page, Integer size);
}
