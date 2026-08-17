package com.pragma.plazoleta.domain.service;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.service.impl.RestaurantCacheServiceImpl;
import com.pragma.plazoleta.domain.spi.IRestaurantCachePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantCacheServiceTest {

    @Mock
    private IRestaurantCachePort restaurantCachePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    private IRestaurantCacheService restaurantCacheService;

    @BeforeEach
    void setUp() {
        restaurantCacheService = new RestaurantCacheServiceImpl(
                restaurantPersistencePort,
                restaurantCachePort
        );
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
    @DisplayName("Should return correct restaurant id successfully")
    @ValueSource(booleans = {true, false})
    void shouldReturnCorrectRestaurantIdSuccessfully(boolean cacheExists) {
        var restaurantId = 1L;
        var restaurant = buildRestaurant(restaurantId);

        if (cacheExists) {
            when(restaurantCachePort.getRestaurantById(restaurantId))
                    .thenReturn(Optional.of(restaurant));
        } else {
            when(restaurantCachePort.getRestaurantById(restaurantId))
                    .thenReturn(Optional.empty());
            when(restaurantPersistencePort.findById(restaurantId))
                    .thenReturn(restaurant);
        }

        var restaurantReturned = restaurantCacheService.getRestaurantById(restaurantId);

        assertNotNull(restaurantReturned);
        assertEquals(restaurant.getId(), restaurantReturned.getId());
    }

    @Test
    @DisplayName("Should return restaurant with id after saved")
    void shouldReturnRestaurantWithIdAfterSaved() {
        var restaurantId = 1L;
        var restaurant = buildRestaurant(null);

        when(restaurantPersistencePort.save(restaurant))
                .thenAnswer(it -> {
                    Restaurant restaurantPassed = it.getArgument(0);
                    restaurantPassed.setId(restaurantId);

                    return restaurantPassed;
                });

        var restaurantReturned = restaurantCacheService.saveRestaurant(restaurant);

        assertNotNull(restaurantReturned);
        assertEquals(restaurantId, restaurantReturned.getId());
    }

    @ParameterizedTest
    @DisplayName("Should return correct restaurant list successfully")
    @ValueSource(booleans = {true, false})
    void shouldReturnCorrectRestaurantListSuccessfully(boolean cacheExists) {
        var pageNumber = 0;
        var pageSize = 10;
        var totalElements = 3L;
        var totalPages = 1;
        var restaurantList = List.of(
                buildRestaurant(1L),
                buildRestaurant(2L),
                buildRestaurant(3L)
        );

        var restaurantPagedResult = PagedResult.of(
                restaurantList,
                pageNumber,
                pageSize,
                totalElements,
                totalPages
        );

        if (cacheExists) {
            when(restaurantCachePort.getRestaurantList(pageNumber, pageSize))
                    .thenReturn(Optional.of(restaurantPagedResult));
        } else {
            when(restaurantCachePort.getRestaurantList(pageNumber, pageSize))
                    .thenReturn(Optional.empty());
            when(restaurantPersistencePort.findAllPaginatedByNameAsc(pageNumber, pageSize))
                    .thenReturn(restaurantPagedResult);
        }

        var restaurantPagedResultReturned = restaurantCacheService.listRestaurants(pageNumber, pageSize);

        assertNotNull(restaurantPagedResultReturned);
        assertEquals(restaurantPagedResult.getItems().size(), restaurantPagedResultReturned.getItems().size());
    }
}
