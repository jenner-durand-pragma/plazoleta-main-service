package com.pragma.plazoleta.domain.service;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.service.impl.DishCacheServiceImpl;
import com.pragma.plazoleta.domain.spi.IDishCachePort;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DishCacheServiceTest {

    @Mock
    private IDishCachePort dishCachePort;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private ICategoryCacheService categoryCacheService;

    @Mock
    private IRestaurantCacheService restaurantCacheService;

    private IDishCacheService dishCacheService;

    @BeforeEach
    void setUp() {
        dishCacheService = new DishCacheServiceImpl(
                dishCachePort,
                dishPersistencePort,
                categoryCacheService,
                restaurantCacheService
        );
    }

    private Dish buildDish(Long id, Long categoryId, Long restaurantId) {
        return Dish.builder()
                .id(id)
                .name("Pepperoni Pizza")
                .description("It's so tasty!")
                .price(20000)
                .imageUrl("https://dishes.example.com/dish.png")
                .active(true)
                .category(Category.builder().id(categoryId).build())
                .restaurant(Restaurant.builder().id(restaurantId).build())
                .build();
    }

    private Category buildCategory(Long id) {
        return Category.builder()
                .id(id)
                .name("Main Course")
                .description("Main dishes")
                .build();
    }

    private Restaurant buildRestaurant(Long id) {
        return Restaurant.builder()
                .id(id)
                .name("Pizza Place")
                .address("Example Street 123")
                .ownerId(5L)
                .phone("+573005698325")
                .logoUrl("https://logo.example.com/image.png")
                .nit("9001234567")
                .build();
    }

    @ParameterizedTest
    @DisplayName("Should return correct dish id successfully")
    @ValueSource(booleans = {true, false})
    void shouldReturnCorrectDishIdSuccessfully(boolean cacheExists) {
        var dishId = 1L;
        var categoryId = 3L;
        var restaurantId = 5L;
        var dish = buildDish(dishId, categoryId, restaurantId);
        var category = buildCategory(categoryId);
        var restaurant = buildRestaurant(restaurantId);

        if (cacheExists) {
            when(dishCachePort.getDishById(dishId))
                    .thenReturn(Optional.of(dish));
        } else {
            when(dishCachePort.getDishById(dishId))
                    .thenReturn(Optional.empty());
            when(dishPersistencePort.findById(dishId))
                    .thenReturn(dish);
        }

        when(restaurantCacheService.getRestaurantById(restaurantId))
                .thenReturn(restaurant);
        when(categoryCacheService.getCategoryById(categoryId))
                .thenReturn(category);

        var dishReturned = dishCacheService.getDishById(dishId);

        assertNotNull(dishReturned);
        assertEquals(dish.getId(), dishReturned.getId());
        assertEquals(restaurant.getId(), dishReturned.getRestaurant().getId());
        assertEquals(category.getId(), dishReturned.getCategory().getId());

        if (cacheExists) {
            verify(dishCachePort, never()).saveDish(dish);
        } else {
            verify(dishCachePort).saveDish(dish);
        }
    }

    @Test
    @DisplayName("Should return dish with id after saved")
    void shouldReturnDishWithIdAfterSaved() {
        var dishId = 1L;
        var dish = buildDish(null, 3L, 5L);

        when(dishPersistencePort.save(dish))
                .thenAnswer(it -> {
                    Dish dishPassed = it.getArgument(0);
                    dishPassed.setId(dishId);

                    return dishPassed;
                });

        var dishReturned = dishCacheService.saveDish(dish);

        assertNotNull(dishReturned);
        assertEquals(dishId, dishReturned.getId());
        verify(dishCachePort).saveDish(dishReturned);
        verify(dishCachePort).deleteDishPagesCached();
    }

    @ParameterizedTest
    @DisplayName("Should return correct dish list successfully")
    @ValueSource(booleans = {true, false})
    void shouldReturnCorrectDishListSuccessfully(boolean cacheExists) {
        var restaurantId = 5L;
        var categoryId = 3L;
        var pageNumber = 0;
        var pageSize = 10;
        var totalElements = 3L;
        var totalPages = 1;
        var dishList = List.of(
                buildDish(1L, categoryId, restaurantId),
                buildDish(2L, categoryId, restaurantId),
                buildDish(3L, categoryId, restaurantId)
        );

        var dishPagedResult = PagedResult.of(
                dishList,
                pageNumber,
                pageSize,
                totalElements,
                totalPages
        );

        if (cacheExists) {
            when(dishCachePort.getDishList(restaurantId, categoryId, pageNumber, pageSize))
                    .thenReturn(Optional.of(dishPagedResult));
        } else {
            when(dishCachePort.getDishList(restaurantId, categoryId, pageNumber, pageSize))
                    .thenReturn(Optional.empty());
            when(dishPersistencePort.findActiveByRestaurantAndCategoryPaginated(
                    restaurantId, categoryId, pageNumber, pageSize))
                    .thenReturn(dishPagedResult);
        }

        var dishPagedResultReturned = dishCacheService.listDishes(restaurantId, categoryId, pageNumber, pageSize);

        assertNotNull(dishPagedResultReturned);
        assertEquals(dishPagedResult.getItems().size(), dishPagedResultReturned.getItems().size());

        if (cacheExists) {
            verify(dishCachePort, never())
                    .saveDishList(restaurantId, categoryId, pageNumber, pageSize, dishPagedResult);
        } else {
            verify(dishCachePort)
                    .saveDishList(restaurantId, categoryId, pageNumber, pageSize, dishPagedResult);
        }
    }
}