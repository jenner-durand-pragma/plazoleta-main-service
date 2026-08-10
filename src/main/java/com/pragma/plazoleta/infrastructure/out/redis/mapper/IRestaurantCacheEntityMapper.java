package com.pragma.plazoleta.infrastructure.out.redis.mapper;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.out.redis.entity.RestaurantCacheEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IRestaurantCacheEntityMapper {

    RestaurantCacheEntity toEntity(Restaurant model);
    Restaurant toModel(RestaurantCacheEntity entity);
}
