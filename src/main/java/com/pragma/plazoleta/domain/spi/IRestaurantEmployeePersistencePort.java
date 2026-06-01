package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;

import java.util.Optional;

public interface IRestaurantEmployeePersistencePort {

    RestaurantEmployee save(RestaurantEmployee employee);

    Optional<Long> findRestaurantIdByUserId(Long userId);
}
