package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.dish.CreateDishRequestDto;
import com.pragma.plazoleta.application.dto.request.dish.UpdateDishRequestDto;
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
import org.mockito.ArgumentCaptor;
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

    private static final Long OWNER_ID = 2L;

    @BeforeEach
    void setUp() {
        requestDto = CreateDishRequestDto.builder()
                .name("Pineapple Pizza")
                .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                .price(15000)
                .categoryId(1L)
                .restaurantId(10L)
                .imageUrl("https://dishes.example.com/dish.png")
                .build();

        var mainCourse = Category.builder()
                .id(1L)
                .name("Main Course")
                .description("Main dishes")
                .build();
        var restaurant = Restaurant.builder()
                .id(10L)
                .name("Pizza Place")
                .ownerId(OWNER_ID)
                .build();

        saved = Dish.builder()
                .id(1L)
                .name("Pineapple Pizza")
                .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                .price(15000)
                .category(mainCourse)
                .restaurant(restaurant)
                .imageUrl("https://dishes.example.com/dish.png")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should create dish")
    void shouldCreateDish() {
        when(dishServicePort.createDish(any(Dish.class), eq(2L))).thenReturn(saved);

        var result = dishHandler.createDish(requestDto, OWNER_ID);

        var dishCaptor = ArgumentCaptor.forClass(Dish.class);
        verify(dishServicePort).createDish(dishCaptor.capture(), eq(OWNER_ID));
        var passedDish = dishCaptor.getValue();

        assertThat(passedDish.getName()).isEqualTo("Pineapple Pizza");
        assertThat(passedDish.getPrice()).isEqualTo(15000);
        assertThat(passedDish.getCategory().getId()).isEqualTo(1L);
        assertThat(passedDish.getRestaurant().getId()).isEqualTo(10L);

        verify(dishServicePort).createDish(any(Dish.class), eq(2L));
        verify(dishRequestMapper).toDish(requestDto);
        verify(dishResponseMapper).toResponse(saved);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getActive()).isTrue();
    }

    @Test
    @DisplayName("Should update price and description in dish")
    void shouldUpdateDishPriceAndDescription() {
        var updateRequest = UpdateDishRequestDto.builder()
                .price(20000)
                .description("Change Description")
                .build();

        when(dishServicePort.updateDish(
                2L,
                20000,
                "Change Description",
                OWNER_ID)
        ).thenReturn(saved);

        var result = dishHandler.updateDish(2L, updateRequest, OWNER_ID);

        verify(dishServicePort).updateDish(2L, 20000, "Change Description", OWNER_ID);
        verify(dishResponseMapper).toResponse(saved);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
}
