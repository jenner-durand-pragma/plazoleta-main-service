package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.restaurant.CreateRestaurantRequestDto;
import com.pragma.plazoleta.application.handler.impl.RestaurantHandler;
import com.pragma.plazoleta.application.mapper.IRestaurantRequestMapper;
import com.pragma.plazoleta.application.mapper.IRestaurantResponseMapper;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantHandlerTest {

    @Mock
    private IRestaurantServicePort restaurantServicePort;

    @Spy
    private IRestaurantRequestMapper restaurantRequestMapper = Mappers.getMapper(IRestaurantRequestMapper.class);

    @Spy
    private IRestaurantResponseMapper restaurantResponseMapper = Mappers.getMapper(IRestaurantResponseMapper.class);

    @InjectMocks
    private RestaurantHandler restaurantHandler;

    private CreateRestaurantRequestDto createRestaurantRequestDto;
    private Restaurant savedRestaurant;

    @BeforeEach
    void setUp() {
        savedRestaurant = Restaurant.builder()
                .name("Pizza Place")
                .address("Example Street 123")
                .ownerId(5L)
                .phone("+573005698325")
                .logoUrl("https://logo.example.com/image.png")
                .nit("9001234567")
                .build();

        createRestaurantRequestDto = CreateRestaurantRequestDto.builder()
                .name("Pizza Place")
                .address("Example Street 123")
                .ownerId(5L)
                .phone("+573005698325")
                .logoUrl("https://logo.example.com/image.png")
                .nit("9001234567")
                .build();
    }

    @Test
    @DisplayName("Should create a restaurant")
    void shouldCreateRestaurant() {
        when(restaurantServicePort.createRestaurant(any(Restaurant.class)))
                .thenReturn(savedRestaurant);

        var result = restaurantHandler.createRestaurant(createRestaurantRequestDto);

        var restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);
        verify(restaurantServicePort).createRestaurant(restaurantCaptor.capture());
        var passedRestaurant = restaurantCaptor.getValue();

        assertThat(passedRestaurant.getNit()).isEqualTo(createRestaurantRequestDto.getNit());
        assertThat(passedRestaurant.getName()).isEqualTo(createRestaurantRequestDto.getName());

        verify(restaurantRequestMapper).toRestaurant(createRestaurantRequestDto);
        verify(restaurantResponseMapper).toResponse(savedRestaurant);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedRestaurant.getId());
        assertThat(result.getName()).isEqualTo(savedRestaurant.getName());
        assertThat(result.getNit()).isEqualTo(savedRestaurant.getNit());
    }

    @Test
    @DisplayName(
            "Should return paginated restaurant list successfully " +
            "when parameters are valid in list restaurants"
    )
    void shouldReturnPaginatedRestaurantListSuccessfullyWhenParametersAreValidInListRestaurants() {
        var restaurant1 = Restaurant.builder()
                .id(1L)
                .name("Apple Place")
                .logoUrl("https://logo.example.com/apple.png")
                .build();
        var restaurant2 = Restaurant.builder()
                .id(2L)
                .name("Burger Place")
                .logoUrl("https://logo.example.com/burger.png")
                .build();
        var pagedResult = PagedResult.of(
                List.of(restaurant1, restaurant2), 0, 10, 2L, 1
        );

        when(restaurantServicePort.listRestaurants(0, 10)).thenReturn(pagedResult);

        var result = restaurantHandler.listRestaurants(0, 10);

        verify(restaurantServicePort).listRestaurants(0, 10);
        verify(restaurantResponseMapper).toListItem(restaurant1);
        verify(restaurantResponseMapper).toListItem(restaurant2);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Apple Place");
        assertThat(result.getItems().get(1).getName()).isEqualTo("Burger Place");
        assertThat(result.getPage()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(2L);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }
}
