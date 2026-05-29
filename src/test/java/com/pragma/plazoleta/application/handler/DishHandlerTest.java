package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.dish.CreateDishRequestDto;
import com.pragma.plazoleta.application.handler.impl.DishHandler;
import com.pragma.plazoleta.application.mapper.IDishRequestMapper;
import com.pragma.plazoleta.application.mapper.IDishResponseMapper;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DishHandlerTest {

    @Mock
    private IDishServicePort dishServicePort;

    @Spy
    private IDishRequestMapper dishRequestMapper = Mappers.getMapper(IDishRequestMapper.class);

    @Spy
    private IDishResponseMapper dishResponseMapper = Mappers.getMapper(IDishResponseMapper.class);

    @InjectMocks
    private DishHandler dishHandler;

    private CreateDishRequestDto requestDto;
    private Dish saved;

    @BeforeEach
    void setUp() {
        requestDto = CreateDishRequestDto.builder()
                .name("Pineapple Pizza")
                .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                .price(15000)
                .categoryId(1L)
                .restaurantId(10L)
                .ownerId(2L)
                .imageUrl("https://dishes.example.com/dish.png")
                .build();

        var categoryReference = Category.builder().id(1L).build();
        var restaurantReference = Restaurant.builder().id(10L).build();

        saved = Dish.builder()
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
    @DisplayName("Should create dish")
    void shouldCreateDish() {
        when(dishServicePort.createDish(any(Dish.class), eq(2L))).thenReturn(saved);

        var result = dishHandler.createDish(requestDto);

        verify(dishServicePort).createDish(any(Dish.class), eq(2L));
        verify(dishRequestMapper).toDish(requestDto);
        verify(dishResponseMapper).toResponse(saved);


        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getActive()).isTrue();
    }
}
