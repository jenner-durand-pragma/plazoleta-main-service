package com.pragma.plazoleta.infrastructure.out.redis.adapter;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.out.redis.container.EmbeddedRedisExtension;
import com.pragma.plazoleta.infrastructure.out.redis.mapper.IDishCacheEntityMapper;
import com.pragma.plazoleta.infrastructure.out.redis.mapper.IDishCacheEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.redis.repository.IDishCacheRepository;
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
        IDishCacheEntityMapperImpl.class
})
class DishCacheAdapterTest {

    private static EmbeddedRedisExtension redisExtension;

    @BeforeAll
    static void start() throws IOException {
        redisExtension = new EmbeddedRedisExtension();
        redisExtension.start();
    }

    @AfterAll
    static void stop() throws IOException {
        redisExtension.stop();
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", () -> redisExtension.getHost());
        registry.add("spring.redis.port", () -> redisExtension.getPort());
    }

    @Autowired
    private IDishCacheRepository dishCacheRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IDishCacheEntityMapper dishCacheEntityMapper;

    private DishCacheAdapter dishCacheAdapter;

    @BeforeEach
    void setUp() {
        dishCacheAdapter = new DishCacheAdapter(
                dishCacheRepository,
                dishCacheEntityMapper,
                redisTemplate
        );
    }

    private Dish buildDish() {
        var categoryReference = Category.builder()
                .id(1L)
                .name("Main Course")
                .description("Main dishes")
                .build();

        var restaurantReference = Restaurant.builder()
                .id(2L)
                .name("Pizza Place")
                .address("Example Street 123")
                .ownerId(5L)
                .phone("+573005698325")
                .logoUrl("https://logo.example.com/image.png")
                .nit("9001234567")
                .build();

        return Dish.builder()
                .id(1L)
                .name("Pineapple Pizza")
                .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                .price(15000)
                .category(categoryReference)
                .restaurant(restaurantReference)
                .imageUrl("https://dishes.example.com/dish.png")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should return saved dish successfully")
    void shouldReturnSavedDishSuccessfully() {
        var dish = buildDish();

        dishCacheAdapter.saveDish(dish);

        var dishCacheResult = dishCacheAdapter.getDishById(dish.getId());

        assertTrue(dishCacheResult.isPresent());
        assertEquals(dish.getId(), dishCacheResult.get().getId());
        assertEquals(dish.getName(), dishCacheResult.get().getName());
    }

    @Test
    @DisplayName("Should return list dish successfully")
    void shouldReturnListDishSuccessfully() {
        var dishOne = buildDish();
        dishOne.setId(1L);

        var dishTwo = buildDish();
        dishTwo.setId(2L);

        var dishThree = buildDish();
        dishThree.setId(3L);

        var dishList = List.of(dishOne, dishTwo, dishThree);
        var pageNumber = 1;
        var pageSize = 3;
        var totalElements = 10L;
        var totalPages = 4;

        var dishPagedResult = PagedResult.of(dishList, pageNumber, pageSize, totalElements, totalPages);

        dishCacheAdapter.saveDishList(2L, 1L, pageNumber, pageSize, dishPagedResult);

        var dishPagedResultCached = dishCacheAdapter.getDishList(2L, 1L, pageNumber, pageSize);

        assertTrue(dishPagedResultCached.isPresent());
        assertEquals(dishList.size(), dishPagedResultCached.get().getItems().size());
        assertEquals(pageNumber, dishPagedResultCached.get().getPage());
        assertEquals(pageSize, dishPagedResultCached.get().getSize());
        assertEquals(totalElements, dishPagedResultCached.get().getTotalElements());
        assertEquals(totalPages, dishPagedResultCached.get().getTotalPages());
    }

    @Test
    @DisplayName("Should return dish empty list when delete list cached")
    void shouldReturnDishEmptyListWhenDeleteListCached() {
        var dishOne = buildDish();
        dishOne.setId(1L);

        var dishTwo = buildDish();
        dishTwo.setId(2L);

        var dishThree = buildDish();
        dishThree.setId(3L);

        var dishList = List.of(dishOne, dishTwo, dishThree);
        var pageNumber = 1;
        var pageSize = 3;
        var totalElements = 10L;
        var totalPages = 4;

        var dishPagedResult = PagedResult.of(dishList, pageNumber, pageSize, totalElements, totalPages);

        dishCacheAdapter.saveDishList(2L, 1L, pageNumber, pageSize, dishPagedResult);

        dishCacheAdapter.deleteDishPagesCached();

        var dishPagedResultCached = dishCacheAdapter.getDishList(2L, 1L, pageNumber, pageSize);

        assertTrue(dishPagedResultCached.isEmpty());
    }
}
