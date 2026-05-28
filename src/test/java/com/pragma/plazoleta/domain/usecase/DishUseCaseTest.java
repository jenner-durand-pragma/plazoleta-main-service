package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.category.CategoryNotFoundException;
import com.pragma.plazoleta.domain.exception.dish.DishOwnershipException;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DishUseCaseTest {

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private ICategoryPersistencePort categoryPersistencePort;

    @InjectMocks
    private DishUseCase dishUseCase;

    private Dish validDish;
    private Category mainCourse;
    private Restaurant restaurant;

    private static final Long OWNER_ID = 2L;

    @BeforeEach
    void setUp() {
        mainCourse = Category.builder()
                .id(1L)
                .name("Main Course")
                .description("Main dishes")
                .build();
        restaurant = Restaurant.builder()
                .id(10L)
                .name("Pizza Place")
                .ownerId(OWNER_ID)
                .build();

        var categoryReference = Category.builder().id(1L).build();
        var restaurantReference = Restaurant.builder().id(10L).build();

        validDish = Dish.builder()
                .name("Pineapple Pizza")
                .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                .price(15000)
                .category(categoryReference)
                .restaurant(restaurantReference)
                .imageUrl("https://dishes.example.com/dish.png")
                .build();
    }

    @Test
    @DisplayName("Should create a dish and set active 'true' by default")
    void shouldCreateDishAndDefaultActiveTrue() {
        when(categoryPersistencePort.findById(1L))
                .thenReturn(mainCourse);
        when(restaurantPersistencePort.findById(10L))
                .thenReturn(restaurant);
        when(dishPersistencePort.save(any(Dish.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = dishUseCase.createDish(validDish, OWNER_ID);

        var captor = ArgumentCaptor.forClass(Dish.class);
        verify(dishPersistencePort).save(captor.capture());

        assertThat(captor.getValue().getActive()).isTrue();
        assertThat(result.getActive()).isTrue();
        assertThat(result.getCategory().getName()).isEqualTo("Main Course");
        assertThat(result.getRestaurant().getOwnerId()).isEqualTo(OWNER_ID);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when category does not exist")
    void shouldThrowWhenCategoryDoesNotExist() {
        when(categoryPersistencePort.findById(1L)).thenReturn(null);

        assertThatThrownBy(() -> dishUseCase.createDish(validDish, OWNER_ID))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(dishPersistencePort, never()).save(any(Dish.class));
    }

    @Test
    @DisplayName("Should throw RestaurantNotFoundException when restaurant does not exist")
    void shouldThrowWhenRestaurantDoesNotExist() {
        when(categoryPersistencePort.findById(1L)).thenReturn(mainCourse);
        when(restaurantPersistencePort.findById(10L)).thenReturn(null);

        assertThatThrownBy(() -> dishUseCase.createDish(validDish, OWNER_ID))
                .isInstanceOf(RestaurantNotFoundException.class);

        verify(dishPersistencePort, never()).save(any(Dish.class));
    }

    @Test
    @DisplayName("Should throw DishOwnershipException when the user is not the restaurant owner")
    void shouldThrowWhenUserIsNotTheOwner() {
        var otherOwnerRestaurant = new Restaurant();
        otherOwnerRestaurant.setId(10L);
        otherOwnerRestaurant.setOwnerId(99L);

        when(categoryPersistencePort.findById(1L)).thenReturn(mainCourse);
        when(restaurantPersistencePort.findById(10L)).thenReturn(otherOwnerRestaurant);

        assertThatThrownBy(() -> dishUseCase.createDish(validDish, OWNER_ID))
                .isInstanceOf(DishOwnershipException.class);

        verify(dishPersistencePort, never()).save(any(Dish.class));
    }
}
