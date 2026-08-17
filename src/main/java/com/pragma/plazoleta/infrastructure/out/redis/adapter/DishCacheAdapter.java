package com.pragma.plazoleta.infrastructure.out.redis.adapter;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.spi.IDishCachePort;
import com.pragma.plazoleta.infrastructure.out.redis.mapper.IDishCacheEntityMapper;
import com.pragma.plazoleta.infrastructure.out.redis.repository.IDishCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

@RequiredArgsConstructor
public class DishCacheAdapter implements IDishCachePort {

    private final IDishCacheRepository dishCacheRepository;
    private final IDishCacheEntityMapper dishCacheEntityMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Optional<Dish> getDishById(Long id) {
        return null;
    }

    @Override
    public void saveDish(Dish dish) {
    }

    @Override
    public Optional<PagedResult<Dish>> getDishList(Long restaurantId, Long categoryId, Integer page, Integer size) {
        return null;
    }

    @Override
    public void saveDishList(Long restaurantId, Long categoryId, Integer page, Integer size, PagedResult<Dish> list) {
    }

    @Override
    public void deleteDishPagesCached() {
    }
}
