package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.exception.order.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.order.DuplicatedOrderItemException;
import com.pragma.plazoleta.domain.exception.order.InvalidOrderDishesException;
import com.pragma.plazoleta.domain.exception.order.OrderItemsEmptyException;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderItem;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private Restaurant validRestaurant;
    private Dish validDish1;
    private Dish validDish2;
    private Order validOrder;

    private static final Long CLIENT_ID = 5L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Long DISH_1_ID = 1L;
    private static final Long DISH_2_ID = 2L;

    @BeforeEach
    void setUp() {
        validRestaurant = Restaurant.builder()
                .id(RESTAURANT_ID)
                .name("Pizza Place")
                .ownerId(2L)
                .build();

        validDish1 = Dish.builder()
                .id(DISH_1_ID)
                .name("Pineapple Pizza")
                .price(15000)
                .restaurant(validRestaurant)
                .active(true)
                .build();

        validDish2 = Dish.builder()
                .id(DISH_2_ID)
                .name("Rice With Chicken")
                .price(3000)
                .restaurant(validRestaurant)
                .active(true)
                .build();

        var restaurantRef = Restaurant.builder().id(RESTAURANT_ID).build();

        var item1Reference = OrderItem.builder()
                .dishId(validDish1.getId())
                .dish(Dish.builder().id(DISH_1_ID).build())
                .quantity(2)
                .build();
        var item2Reference = OrderItem.builder()
                .dishId(validDish2.getId())
                .dish(Dish.builder().id(DISH_2_ID).build())
                .quantity(1)
                .build();

        validOrder = Order.builder()
                .restaurant(restaurantRef)
                .items(List.of(item1Reference, item2Reference))
                .build();
    }

    @Test
    @DisplayName("Should create order with status PENDING when all data is valid in create order")
    void shouldCreateOrderWithStatusPendingWhenAllDataIsValidInCreateOrder() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID))
                .thenReturn(validRestaurant);
        when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID))
                .thenReturn(false);
        when(dishPersistencePort.findAllByIdIn(List.of(DISH_1_ID, DISH_2_ID)))
                .thenReturn(List.of(validDish1, validDish2));
        when(orderPersistencePort.save(any(Order.class)))
                .thenAnswer(inv -> {
                    var o = (Order) inv.getArgument(0);
                    o.setId(100L);
                    o.getItems().forEach(oi -> oi.setOrderId(o.getId()));

                    return o;
                });

        var result = orderUseCase.createOrder(validOrder, CLIENT_ID);

        var captor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).save(captor.capture());
        var persisted = captor.getValue();

        assertThat(persisted.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(persisted.getClientId()).isEqualTo(CLIENT_ID);
        assertThat(persisted.getChefId()).isNull();
        assertThat(persisted.getOrderDate()).isNotNull();
        assertThat(persisted.getItems()).hasSize(2);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getItems().get(0).getOrderId()).isEqualTo(100L);
        assertThat(result.getItems().get(1).getOrderId()).isEqualTo(100L);
        assertThat(result.getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should throw OrderItemsEmptyException when items list is empty in create order")
    void shouldThrowOrderItemsEmptyExceptionWhenItemsListIsEmptyInCreateOrder() {
        var emptyOrder = Order.builder()
                .restaurant(Restaurant.builder().id(RESTAURANT_ID).build())
                .items(List.of())
                .build();

        assertThatThrownBy(() -> orderUseCase.createOrder(emptyOrder, CLIENT_ID))
                .isInstanceOf(OrderItemsEmptyException.class);

        verify(restaurantPersistencePort, never()).findById(any());
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicatedOrderItemException when items contain duplicate dishIds in create order")
    void shouldThrowDuplicatedOrderItemExceptionWhenItemsContainDuplicateDishIdsInCreateOrder() {
        var duplicatedOrder = Order.builder()
                .restaurant(Restaurant.builder().id(RESTAURANT_ID).build())
                .items(List.of(
                        OrderItem.builder().dish(Dish.builder().id(DISH_1_ID).build()).quantity(2).build(),
                        OrderItem.builder().dish(Dish.builder().id(DISH_1_ID).build()).quantity(1).build()
                ))
                .build();

        assertThatThrownBy(() -> orderUseCase.createOrder(duplicatedOrder, CLIENT_ID))
                .isInstanceOf(DuplicatedOrderItemException.class);

        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RestaurantNotFoundException when the restaurant does not exist in create order")
    void shouldThrowRestaurantNotFoundExceptionWhenTheRestaurantDoesNotExistInCreateOrder() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(null);

        assertThatThrownBy(() -> orderUseCase.createOrder(validOrder, CLIENT_ID))
                .isInstanceOf(RestaurantNotFoundException.class);

        verify(orderPersistencePort, never()).existsActiveOrderByClientId(any());
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ClientHasActiveOrderException when the client already has an active order in create order")
    void shouldThrowClientHasActiveOrderExceptionWhenTheClientAlreadyHasAnActiveOrderInCreateOrder() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID))
                .thenReturn(validRestaurant);
        when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID))
                .thenReturn(true);

        assertThatThrownBy(() -> orderUseCase.createOrder(validOrder, CLIENT_ID))
                .isInstanceOf(ClientHasActiveOrderException.class);

        verify(dishPersistencePort, never()).findAllByIdIn(any());
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidOrderDishesException when one of the dishes does not exist in create order")
    void shouldThrowInvalidOrderDishesExceptionWhenOneOfTheDishesDoesNotExistInCreateOrder() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID))
                .thenReturn(validRestaurant);
        when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID))
                .thenReturn(false);

        when(dishPersistencePort.findAllByIdIn(List.of(DISH_1_ID, DISH_2_ID)))
                .thenReturn(List.of(validDish1));

        assertThatThrownBy(() -> orderUseCase.createOrder(validOrder, CLIENT_ID))
                .isInstanceOf(InvalidOrderDishesException.class);

        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidOrderDishesException when a dish is inactive in create order")
    void shouldThrowInvalidOrderDishesExceptionWhenADishIsInactiveInCreateOrder() {
        var inactiveDish = Dish.builder()
                .id(DISH_2_ID)
                .name("Spaghetti")
                .restaurant(validRestaurant)
                .active(false)
                .build();

        when(restaurantPersistencePort.findById(RESTAURANT_ID))
                .thenReturn(validRestaurant);
        when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID))
                .thenReturn(false);
        when(dishPersistencePort.findAllByIdIn(any()))
                .thenReturn(List.of(validDish1, inactiveDish));

        assertThatThrownBy(() -> orderUseCase.createOrder(validOrder, CLIENT_ID))
                .isInstanceOf(InvalidOrderDishesException.class);

        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName(
            "Should throw InvalidOrderDishesException when " +
            "a dish belongs to a different restaurant in create order"
    )
    void shouldThrowInvalidOrderDishesExceptionWhenADishBelongsToADifferentRestaurantInCreateOrder() {
        var anotherRestaurant = Restaurant.builder()
                .id(99L)
                .name("Another Restaurant")
                .build();
        var anotherDish = Dish.builder()
                .id(DISH_2_ID)
                .name("Another Dish")
                .restaurant(anotherRestaurant)
                .active(true)
                .build();

        when(restaurantPersistencePort.findById(RESTAURANT_ID))
                .thenReturn(validRestaurant);
        when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID))
                .thenReturn(false);
        when(dishPersistencePort.findAllByIdIn(any()))
                .thenReturn(List.of(validDish1, anotherDish));

        assertThatThrownBy(() -> orderUseCase.createOrder(validOrder, CLIENT_ID))
                .isInstanceOf(InvalidOrderDishesException.class);

        verify(orderPersistencePort, never()).save(any());
    }
}
