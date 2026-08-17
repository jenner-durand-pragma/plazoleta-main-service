package com.pragma.plazoleta.infrastructure.out.redis.repository;

import com.pragma.plazoleta.infrastructure.out.redis.entity.DishCacheEntity;
import org.springframework.data.repository.CrudRepository;

public interface IDishCacheRepository extends CrudRepository<DishCacheEntity, Long> {
}
