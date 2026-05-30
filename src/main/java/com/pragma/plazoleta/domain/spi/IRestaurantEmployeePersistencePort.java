package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;

public interface IRestaurantEmployeePersistencePort {

    RestaurantEmployee save(RestaurantEmployee employee);

}
