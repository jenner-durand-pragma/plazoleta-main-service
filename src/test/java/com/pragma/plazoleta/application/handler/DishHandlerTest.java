package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.dish.CreateDishRequestDto;
import com.pragma.plazoleta.application.dto.request.dish.UpdateDishRequestDto;
import com.pragma.plazoleta.application.dto.request.dish.UpdateDishStatusRequestDto;
import com.pragma.plazoleta.application.handler.impl.DishHandler;
import com.pragma.plazoleta.application.mapper.IDishRequestMapper;
import com.pragma.plazoleta.application.mapper.IDishResponseMapper;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.common.PagedResult;
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

import java.util.List;

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
    private Dish savedDish;

    private static final Long OWNER_ID = 2L;
    private static final Long RESTAURANT_ID = 10L;

    @BeforeEach
    void setUp() {
        requestDto = CreateDishRequestDto.builder()
                .name("Pineapple Pizza")
                .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                .price(15000)
                .categoryId(1L)
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

        savedDish = Dish.builder()
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
    @DisplayName("Should create dish successfully when data is valid in create dish")
    void shouldCreateDishSuccessfullyWhenDataIsValidInCreateDish() {
        when(dishServicePort.createDish(any(Dish.class), eq(OWNER_ID))).thenReturn(savedDish);

        var result = dishHandler.createDish(RESTAURANT_ID, requestDto, OWNER_ID);

        var dishCaptor = ArgumentCaptor.forClass(Dish.class);
        verify(dishServicePort).createDish(dishCaptor.capture(), eq(OWNER_ID));
        var passedDish = dishCaptor.getValue();

        assertThat(passedDish.getName()).isEqualTo("Pineapple Pizza");
        assertThat(passedDish.getPrice()).isEqualTo(15000);
        assertThat(passedDish.getCategory().getId()).isEqualTo(1L);

        verify(dishServicePort).createDish(any(Dish.class), eq(2L));
        verify(dishRequestMapper).toDish(requestDto, RESTAURANT_ID);
        verify(dishResponseMapper).toResponse(savedDish);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getActive()).isTrue();
    }

    @Test
    @DisplayName("Should update dish price and description successfully when data is valid in update dish")
    void shouldUpdateDishPriceAndDescriptionSuccessfullyWhenDataIsValidInUpdateDish() {
        var updateRequest = UpdateDishRequestDto.builder()
                .price(20000)
                .description("Change Description")
                .build();
        savedDish.setPrice(20000);
        savedDish.setDescription("Change Description");

        when(dishServicePort.updateDish(
                1L,
                20000,
                "Change Description",
                OWNER_ID)
        ).thenReturn(savedDish);

        var result = dishHandler.updateDish(1L, updateRequest, OWNER_ID);

        verify(dishServicePort).updateDish(1L, 20000, "Change Description", OWNER_ID);
        verify(dishResponseMapper).toResponse(savedDish);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPrice()).isEqualTo(20000);
        assertThat(result.getDescription()).isEqualTo("Change Description");
    }

    @Test
    @DisplayName("Should update dish status successfully when data is valid in update dish status")
    void shouldUpdateDishStatusSuccessfullyWhenDataIsValidInUpdateDishStatus() {
        var updateStatusRequest = UpdateDishStatusRequestDto.builder()
                .active(false)
                .build();

        when(dishServicePort.updateDishStatus(1L, false, OWNER_ID)).thenReturn(savedDish);

        var result = dishHandler.updateDishStatus(1L, updateStatusRequest, OWNER_ID);

        verify(dishServicePort).updateDishStatus(1L, false, OWNER_ID);
        verify(dishResponseMapper).toResponse(savedDish);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName(
            "Should return paginated dishes mapped to list item dto when" +
            "parameters are valid in list dishes by restaurant"
    )
    void shouldReturnPaginatedDishesMappedToListItemDtoWhenParametersAreValidInListDishesByRestaurant() {
        var pagedResult = PagedResult.of(List.of(savedDish), 0, 10, 1L, 1);
        when(dishServicePort.listDishesByRestaurant(10L, 1L, 0, 10))
                .thenReturn(pagedResult);

        var result = dishHandler.listDishesByRestaurant(10L, 1L, 0, 10);

        verify(dishServicePort).listDishesByRestaurant(10L, 1L, 0, 10);
        verify(dishResponseMapper).toListItem(savedDish);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Pineapple Pizza");
        assertThat(result.getPage()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }
}
