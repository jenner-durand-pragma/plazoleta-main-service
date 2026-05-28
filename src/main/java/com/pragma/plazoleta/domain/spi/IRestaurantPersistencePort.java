package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Restaurant;

public interface IRestaurantPersistencePort {

    Restaurant save(Restaurant restaurant);

    Boolean existsByNit(String nit);

}
