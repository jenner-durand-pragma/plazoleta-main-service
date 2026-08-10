package com.pragma.plazoleta.infrastructure.out.redis.adapter;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantCachePort;
import com.pragma.plazoleta.infrastructure.out.redis.entity.RestaurantCacheEntity;
import com.pragma.plazoleta.infrastructure.out.redis.mapper.IRestaurantCacheEntityMapper;
import com.pragma.plazoleta.infrastructure.out.redis.repository.IRestaurantCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class RestaurantCacheAdapter implements IRestaurantCachePort {

    private final IRestaurantCacheRepository restaurantCacheRepository;
    private final IRestaurantCacheEntityMapper restaurantCacheEntityMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final long TTL_INDIVIDUAL_SECONDS = 600;
    private static final long TTL_LIST_SECONDS = 180;
    private static final String LIST_KEY_PREFIX = "restaurant-list:";

    @Override
    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantCacheRepository.findById(id)
                .map(restaurantCacheEntityMapper::toModel);
    }

    @Override
    public void saveRestaurant(Restaurant restaurant) {
        var restaurantCache = restaurantCacheEntityMapper.toEntity(restaurant);
        restaurantCache.setTtl(TTL_INDIVIDUAL_SECONDS);

        restaurantCacheRepository.save(restaurantCache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<PagedResult<Restaurant>> getRestaurantList(Integer page, Integer size) {
        var restaurantListCached = (PagedResult<RestaurantCacheEntity>)
                redisTemplate.opsForValue().get(listKey(page, size));

        return Optional.ofNullable(restaurantListCached)
                .map(l -> l.mapTo(restaurantCacheEntityMapper::toModel));
    }

    @Override
    public void saveRestaurantList(Integer page, Integer size, PagedResult<Restaurant> restaurants) {
        var restaurantListCache = restaurants.mapTo(restaurantCacheEntityMapper::toEntity);

        redisTemplate.opsForValue().set(listKey(page, size), restaurantListCache, TTL_LIST_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void deleteRestaurantPagesCached() {
        var keys = redisTemplate.keys(LIST_KEY_PREFIX + "*");

        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    private String listKey(Integer page, Integer size) {
        return LIST_KEY_PREFIX + page + "-" + size;
    }
}
