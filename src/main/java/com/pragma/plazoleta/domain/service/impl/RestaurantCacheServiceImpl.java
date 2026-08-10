package com.pragma.plazoleta.domain.service.impl;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.service.IRestaurantCacheService;
import com.pragma.plazoleta.domain.spi.IRestaurantCachePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantCacheServiceImpl implements IRestaurantCacheService {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IRestaurantCachePort restaurantCachePort;

    @Override
    public Restaurant getRestaurantById(Long id) {
        return restaurantCachePort.getRestaurantById(id)
                .orElseGet(() -> {
                    var restaurant = restaurantPersistencePort.findById(id);
                    restaurantCachePort.saveRestaurant(restaurant);

                    return restaurant;
                });
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        var restaurantSaved = restaurantPersistencePort.save(restaurant);
        restaurantCachePort.saveRestaurant(restaurantSaved);
        restaurantCachePort.deleteRestaurantPagesCached();

        return restaurantSaved;
    }

    @Override
    public PagedResult<Restaurant> listRestaurants(Integer page, Integer size) {
        return restaurantCachePort.getRestaurantList(page, size)
                .orElseGet(() -> {
                    var restaurantList = restaurantPersistencePort.findAllPaginatedByNameAsc(page, size);
                    restaurantCachePort.saveRestaurantList(page, size, restaurantList);

                    return restaurantList;
                });
    }
}
