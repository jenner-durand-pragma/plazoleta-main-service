package com.pragma.plazoleta.infrastructure.out.redis.repository;

import com.pragma.plazoleta.infrastructure.out.redis.entity.RestaurantCacheEntity;
import org.springframework.data.repository.CrudRepository;

public interface IRestaurantCacheRepository extends CrudRepository<RestaurantCacheEntity, Long> {
}
