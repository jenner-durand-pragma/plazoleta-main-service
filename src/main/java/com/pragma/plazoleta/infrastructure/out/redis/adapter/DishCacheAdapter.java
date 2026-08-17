package com.pragma.plazoleta.infrastructure.out.redis.adapter;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.spi.IDishCachePort;
import com.pragma.plazoleta.infrastructure.out.redis.entity.DishCacheEntity;
import com.pragma.plazoleta.infrastructure.out.redis.mapper.IDishCacheEntityMapper;
import com.pragma.plazoleta.infrastructure.out.redis.repository.IDishCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class DishCacheAdapter implements IDishCachePort {

    private static final long TTL_INDIVIDUAL_SECONDS = 600;
    private static final long TTL_LIST_SECONDS = 180;
    private static final String LIST_KEY_PREFIX = "dish-list:";

    private static final String NO_CATEGORY_FILTER = "all";

    private final IDishCacheRepository dishCacheRepository;
    private final IDishCacheEntityMapper dishCacheEntityMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Optional<Dish> getDishById(Long id) {
        return dishCacheRepository.findById(id)
                .map(dishCacheEntityMapper::toModel);
    }

    @Override
    public void saveDish(Dish dish) {
        var dishCache = dishCacheEntityMapper.toEntity(dish);
        dishCache.setTtl(TTL_INDIVIDUAL_SECONDS);

        dishCacheRepository.save(dishCache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<PagedResult<Dish>> getDishList(Long restaurantId, Long categoryId, Integer page, Integer size) {
        var pagedResultCache = (PagedResult<DishCacheEntity>) redisTemplate.opsForValue().get(
                listKey(restaurantId, categoryId, page, size)
        );

        return Optional.ofNullable(pagedResultCache)
                .map(p -> p.mapTo(dishCacheEntityMapper::toModel));
    }

    @Override
    public void saveDishList(Long restaurantId, Long categoryId, Integer page, Integer size, PagedResult<Dish> list) {
        var listCache = list.mapTo(dishCacheEntityMapper::toEntity);

        redisTemplate.opsForValue().set(
                listKey(restaurantId, categoryId, page, size),
                listCache,
                TTL_LIST_SECONDS,
                TimeUnit.SECONDS
        );
    }

    @Override
    public void deleteDishPagesCached(Long restaurantId) {
        var keys = redisTemplate.keys(LIST_KEY_PREFIX + restaurantId + "-*");

        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    private String listKey(Long restaurantId, Long categoryId, Integer page, Integer size) {
        var cacheCategoryIdKey = categoryId != null ? categoryId : NO_CATEGORY_FILTER;

        return LIST_KEY_PREFIX + restaurantId + "-" + cacheCategoryIdKey + "-" + page + "-" + size;
    }
}
