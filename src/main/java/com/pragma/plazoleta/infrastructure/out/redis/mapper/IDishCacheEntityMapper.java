package com.pragma.plazoleta.infrastructure.out.redis.mapper;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.infrastructure.out.redis.entity.DishCacheEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IDishCacheEntityMapper {

    DishCacheEntity toEntity(Dish model);
    Dish toModel(DishCacheEntity entity);
}
