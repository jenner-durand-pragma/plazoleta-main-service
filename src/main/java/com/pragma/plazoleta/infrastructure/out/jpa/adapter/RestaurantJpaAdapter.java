package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Override
    public Restaurant save(Restaurant restaurant) {
        var entity = restaurantEntityMapper.toEntity(restaurant);
        var saved = restaurantRepository.save(entity);

        return restaurantEntityMapper.toModel(saved);
    }

    @Override
    public Restaurant findById(Long id) {
        return restaurantRepository.findById(id)
                .map(restaurantEntityMapper::toModel)
                .orElse(null);
    }

    @Override
    public Boolean existsByNit(String nit) {
        return restaurantRepository.existsByNit(nit);
    }

    @Override
    public PagedResult<Restaurant> findAllPaginatedByNameAsc(Integer page, Integer size) {
        return null;
    }
}
