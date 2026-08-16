package com.pragma.plazoleta.infrastructure.out.redis.adapter;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.out.redis.container.EmbeddedRedisExtension;
import com.pragma.plazoleta.infrastructure.out.redis.mapper.IRestaurantCacheEntityMapper;
import com.pragma.plazoleta.infrastructure.out.redis.mapper.IRestaurantCacheEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.redis.repository.IRestaurantCacheRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import({
        IRestaurantCacheEntityMapperImpl.class
})
class RestaurantCacheAdapterTest {

    private static EmbeddedRedisExtension redis;

    @BeforeAll
    static void startContainer() throws IOException {
        redis = new EmbeddedRedisExtension();
        redis.start();
    }

    @AfterAll
    static void stopContainer() throws IOException {
        redis.stop();
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", () -> redis.getHost());
        registry.add("spring.redis.port", () -> redis.getPort());
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IRestaurantCacheRepository restaurantCacheRepository;

    @Autowired
    private IRestaurantCacheEntityMapper restaurantCacheEntityMapper;

    private RestaurantCacheAdapter restaurantCacheAdapter;

    @BeforeEach
    void setUp() {
        restaurantCacheAdapter = new RestaurantCacheAdapter(
                restaurantCacheRepository,
                restaurantCacheEntityMapper,
                redisTemplate
        );
    }

    private Restaurant buildRestaurant() {
        return Restaurant.builder()
                .name("Pizza Place")
                .address("Example Street 123")
                .ownerId(5L)
                .phone("+573005698325")
                .logoUrl("https://logo.example.com/image.png")
                .nit("9001234567")
                .build();
    }

    @Test
    @DisplayName("Should return a saved restaurant cached successfully")
    void shouldReturnSavedRestaurantCachedSuccessfully() {
        var restaurant = buildRestaurant();
        restaurant.setId(1L);

        restaurantCacheAdapter.saveRestaurant(restaurant);

        var restaurantCached = restaurantCacheAdapter.getRestaurantById(restaurant.getId());

        assertTrue(restaurantCached.isPresent());
        assertEquals(restaurant.getId(), restaurantCached.get().getId());
        assertEquals(restaurant.getName(), restaurantCached.get().getName());
    }

    @Test
    @DisplayName("Should return restaurant a restaurant list cached successfully")
    void shouldReturnRestaurantListCachedSuccessfully() {
        var restaurantOne = buildRestaurant();
        restaurantOne.setId(1L);

        var restaurantTwo = buildRestaurant();
        restaurantTwo.setId(2L);

        var restaurantThree = buildRestaurant();
        restaurantThree.setId(3L);

        var restaurantList = List.of(restaurantOne, restaurantTwo, restaurantThree);
        var pageNumber = 1;
        var pageSize = 3;
        var totalElements = 10L;
        var totalPages = 4;

        var restaurantPagedResult = PagedResult.of(restaurantList, pageNumber, pageSize, totalElements, totalPages);

        restaurantCacheAdapter.saveRestaurantList(pageNumber, pageSize, restaurantPagedResult);

        var restaurantPagedResultCached = restaurantCacheAdapter.getRestaurantList(pageNumber, pageSize);

        assertTrue(restaurantPagedResultCached.isPresent());
        assertEquals(restaurantList.size(), restaurantPagedResultCached.get().getItems().size());
        assertEquals(pageNumber, restaurantPagedResultCached.get().getPage());
        assertEquals(pageSize, restaurantPagedResultCached.get().getSize());
        assertEquals(totalElements, restaurantPagedResultCached.get().getTotalElements());
        assertEquals(totalPages, restaurantPagedResultCached.get().getTotalPages());
    }

    @Test
    @DisplayName("Should return restaurant empty list when delete list cached")
    void shouldReturnEmptyRestaurantListWhenDeleteListCached() {
        var restaurantOne = buildRestaurant();
        restaurantOne.setId(1L);

        var restaurantTwo = buildRestaurant();
        restaurantTwo.setId(2L);

        var restaurantThree = buildRestaurant();
        restaurantThree.setId(3L);

        var restaurantList = List.of(restaurantOne, restaurantTwo, restaurantThree);
        var pageNumber = 1;
        var pageSize = 3;
        var totalElements = 10L;
        var totalPages = 4;

        var restaurantPagedResult = PagedResult.of(restaurantList, pageNumber, pageSize, totalElements, totalPages);

        restaurantCacheAdapter.saveRestaurantList(pageNumber, pageSize, restaurantPagedResult);

        restaurantCacheAdapter.deleteRestaurantPagesCached();

        var restaurantPagedResultCached = restaurantCacheAdapter.getRestaurantList(pageNumber, pageSize);

        assertTrue(restaurantPagedResultCached.isEmpty());
    }
}
