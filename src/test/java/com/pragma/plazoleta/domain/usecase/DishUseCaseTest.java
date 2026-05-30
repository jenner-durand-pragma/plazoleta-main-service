package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.category.CategoryNotFoundException;
import com.pragma.plazoleta.domain.exception.dish.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantOwnershipException;
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

        validDish = Dish.builder()
                .id(1L)
                .name("Pineapple Pizza")
                .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                .price(15000)
                .category(mainCourse)
                .restaurant(restaurant)
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
    void shouldThrowWhenCategoryDoesNotExistInCreateDish() {
        when(categoryPersistencePort.findById(1L)).thenReturn(null);

        assertThatThrownBy(() -> dishUseCase.createDish(validDish, OWNER_ID))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(dishPersistencePort, never()).save(any(Dish.class));
    }

    @Test
    @DisplayName("Should throw RestaurantNotFoundException when restaurant does not exist")
    void shouldThrowWhenRestaurantDoesNotExistInCreateDish() {
        when(categoryPersistencePort.findById(1L)).thenReturn(mainCourse);
        when(restaurantPersistencePort.findById(10L)).thenReturn(null);

        assertThatThrownBy(() -> dishUseCase.createDish(validDish, OWNER_ID))
                .isInstanceOf(RestaurantNotFoundException.class);

        verify(dishPersistencePort, never()).save(any(Dish.class));
    }

    @Test
    @DisplayName("Should throw DishOwnershipException when the user is not the restaurant owner")
    void shouldThrowWhenUserIsNotTheOwnerInCreateDish() {
        var otherOwnerRestaurant = new Restaurant();
        otherOwnerRestaurant.setId(10L);
        otherOwnerRestaurant.setOwnerId(99L);

        when(categoryPersistencePort.findById(1L)).thenReturn(mainCourse);
        when(restaurantPersistencePort.findById(10L)).thenReturn(otherOwnerRestaurant);

        assertThatThrownBy(() -> dishUseCase.createDish(validDish, OWNER_ID))
                .isInstanceOf(RestaurantOwnershipException.class);

        verify(dishPersistencePort, never()).save(any(Dish.class));
    }

    @Test
    @DisplayName("Should update only price and description when caller is the owner")
    void shouldUpdateDishPriceAndDescription() {
        validDish.setActive(true);

        when(dishPersistencePort.findById(1L))
                .thenReturn(validDish);
        when(dishPersistencePort.save(any(Dish.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = dishUseCase.updateDish(
                1L,
                20000,
                "Updated description",
                OWNER_ID
        );

        var captor = ArgumentCaptor.forClass(Dish.class);
        verify(dishPersistencePort).save(captor.capture());
        var persisted = captor.getValue();

        assertThat(persisted.getPrice()).isEqualTo(20000);
        assertThat(persisted.getDescription()).isEqualTo("Updated description");
        assertThat(persisted.getName()).isEqualTo("Pineapple Pizza");
        assertThat(persisted.getImageUrl()).isEqualTo("https://dishes.example.com/dish.png");
        assertThat(persisted.getCategory().getId()).isEqualTo(1L);
        assertThat(persisted.getRestaurant().getId()).isEqualTo(10L);
        assertThat(persisted.getActive()).isTrue();

        assertThat(persisted.getId()).isNotNull();
        assertThat(result.getPrice()).isEqualTo(20000);
        assertThat(result.getDescription()).isEqualTo("Updated description");
    }

    @Test
    @DisplayName("Should throw DishNotFoundException when the dish does not exist")
    void shouldThrowWhenDishDoesNotExistInUpdateDish() {
        when(dishPersistencePort.findById(1L)).thenReturn(null);

        assertThatThrownBy(() ->
                dishUseCase.updateDish(1L, 20000, "Updated", OWNER_ID))
                .isInstanceOf(DishNotFoundException.class);

        verify(dishPersistencePort, never()).save(any(Dish.class));
    }

    @Test
    @DisplayName("Should throw DishOwnershipException when caller is not the restaurant owner")
    void shouldThrowWhenCallerIsNotTheOwnerInUpdateDish() {
        when(dishPersistencePort.findById(1L)).thenReturn(validDish);

        assertThatThrownBy(() ->
                dishUseCase.updateDish(1L, 20000, "Updated", 999L))
                .isInstanceOf(RestaurantOwnershipException.class);

        verify(dishPersistencePort, never()).save(any(Dish.class));
    }

    @Test
    @DisplayName("Should update only price when description is null")
    void shouldUpdateOnlyPriceWhenDescriptionIsNullInUpdateDish() {
        when(dishPersistencePort.findById(1L)).thenReturn(validDish);
        when(dishPersistencePort.save(any(Dish.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        dishUseCase.updateDish(1L, 25000, null, OWNER_ID);

        var captor = ArgumentCaptor.forClass(Dish.class);
        verify(dishPersistencePort).save(captor.capture());
        var persisted = captor.getValue();

        assertThat(persisted.getPrice()).isEqualTo(25000);
        assertThat(persisted.getDescription())
                .isEqualTo("Classic Hawaiian pizza featuring a perfect balance " +
                        "of sweet juicy pineapple chunks"
                );
    }

    @Test
    @DisplayName("Should update only description when price is null")
    void shouldUpdateOnlyDescriptionWhenPriceIsNullInUpdateDish() {
        when(dishPersistencePort.findById(1L)).thenReturn(validDish);
        when(dishPersistencePort.save(any(Dish.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        dishUseCase.updateDish(1L, null, "Brand new description", OWNER_ID);

        var captor = ArgumentCaptor.forClass(Dish.class);
        verify(dishPersistencePort).save(captor.capture());
        var persisted = captor.getValue();

        assertThat(persisted.getPrice()).isEqualTo(15000);
        assertThat(persisted.getDescription()).isEqualTo("Brand new description");
    }

    @Test
    @DisplayName("Should disable dish when caller is the owner")
    void shouldDisableDish() {
        when(dishPersistencePort.findById(validDish.getId())).thenReturn(validDish);
        when(dishPersistencePort.save(any(Dish.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = dishUseCase.updateDishStatus(validDish.getId(), false, OWNER_ID);

        var captor = ArgumentCaptor.forClass(Dish.class);
        verify(dishPersistencePort).save(captor.capture());

        assertThat(captor.getValue().getActive()).isFalse();
        assertThat(result.getActive()).isFalse();
    }

    @Test
    @DisplayName("Should enable a previously disabled dish")
    void shouldEnableDish() {
        validDish.setActive(false);

        when(dishPersistencePort.findById(validDish.getId())).thenReturn(validDish);
        when(dishPersistencePort.save(any(Dish.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = dishUseCase.updateDishStatus(validDish.getId(), true, OWNER_ID);

        assertThat(result.getActive()).isTrue();
    }
}
