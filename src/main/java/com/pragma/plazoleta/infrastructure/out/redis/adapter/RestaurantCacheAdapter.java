package com.pragma.plazoleta.infrastructure.out.redis.adapter;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantCachePort;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class RestaurantCacheAdapter implements IRestaurantCachePort {

    @Override
    public Optional<Restaurant> getRestaurantById(Long id) {
        return null;
    }

    @Override
    public void saveRestaurant(Restaurant restaurant) {
    }

    @Override
    public Optional<PagedResult<Restaurant>> getRestaurantList(Integer page, Integer size) {
        return null;
    }

    @Override
    public void saveRestaurantList(Integer page, Integer size, PagedResult<Restaurant> restaurants) {
    }

    @Override
    public void deleteRestaurantPagesCached() {
    }
}
