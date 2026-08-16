package com.pragma.plazoleta.infrastructure.out.redis.repository;

import com.pragma.plazoleta.infrastructure.out.redis.entity.CategoryCacheEntity;
import org.springframework.data.repository.CrudRepository;

public interface ICategoryCacheRepository extends CrudRepository<CategoryCacheEntity ,Long> {
}
