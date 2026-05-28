package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Restaurant;

public interface IRestaurantServicePort {

    Restaurant createRestaurant(Restaurant restaurant);

}
