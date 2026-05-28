package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.restaurant.CreateRestaurantRequestDto;
import com.pragma.plazoleta.application.handler.impl.RestaurantHandler;
import com.pragma.plazoleta.application.mapper.IRestaurantRequestMapper;
import com.pragma.plazoleta.application.mapper.IRestaurantResponseMapper;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

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
}
