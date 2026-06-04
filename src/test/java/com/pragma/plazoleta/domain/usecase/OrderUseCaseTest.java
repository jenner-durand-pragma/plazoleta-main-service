package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.exception.common.InvalidPaginationException;
import com.pragma.plazoleta.domain.exception.order.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.order.DuplicatedOrderDishException;
import com.pragma.plazoleta.domain.exception.order.InvalidOrderDishesException;
import com.pragma.plazoleta.domain.exception.order.InvalidOrderStateException;
import com.pragma.plazoleta.domain.exception.order.OrderChefOwnershipException;
import com.pragma.plazoleta.domain.exception.order.OrderDishesEmptyException;
import com.pragma.plazoleta.domain.exception.order.OrderEmployeeOwnershipException;
import com.pragma.plazoleta.domain.exception.order.OrderNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurantemployee.EmployeeWithoutRestaurantException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.INotificationPort;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    @Mock
    private IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;

    @Mock
    private INotificationPort notificationPort;

    @Mock
    private IUserInformationPort userInformationPort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private Restaurant validRestaurant;
    private Dish validDish1;
    private Dish validDish2;
    private Order validOrder;
    private Order pendingOrder;
    private Order inPreparationOrder;
    private UserInformation customer;

    private static final Long CLIENT_ID = 5L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Long EMPLOYEE_ID = 7L;
    private static final Long ORDER_ID = 42L;
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

        var item1Reference = OrderDish.builder()
                .dishId(validDish1.getId())
                .dish(Dish.builder().id(DISH_1_ID).build())
                .quantity(2)
                .build();
        var item2Reference = OrderDish.builder()
                .dishId(validDish2.getId())
                .dish(Dish.builder().id(DISH_2_ID).build())
                .quantity(1)
                .build();

        validOrder = Order.builder()
                .restaurant(restaurantRef)
                .items(List.of(item1Reference, item2Reference))
                .build();

        pendingOrder = Order.builder()
                .id(ORDER_ID)
                .clientId(CLIENT_ID)
                .restaurant(restaurantRef)
                .status(OrderStatus.PENDING)
                .chefId(null)
                .orderDate(LocalDateTime.now())
                .items(List.of())
                .build();

        inPreparationOrder = Order.builder()
                .id(ORDER_ID)
                .clientId(CLIENT_ID)
                .restaurant(restaurantRef)
                .status(OrderStatus.IN_PREPARATION)
                .chefId(EMPLOYEE_ID)
                .orderDate(LocalDateTime.now())
                .items(List.of())
                .build();

        customer = UserInformation.builder()
                .id(CLIENT_ID)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("72839422")
                .phone("+5198576854")
                .email("jenner.durand@plazoleta.com")
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
    @DisplayName("Should throw OrderDishesEmptyException when items list is empty in create order")
    void shouldThrowOrderDishesEmptyExceptionWhenItemsListIsEmptyInCreateOrder() {
        var emptyOrder = Order.builder()
                .restaurant(Restaurant.builder().id(RESTAURANT_ID).build())
                .items(List.of())
                .build();

        assertThatThrownBy(() -> orderUseCase.createOrder(emptyOrder, CLIENT_ID))
                .isInstanceOf(OrderDishesEmptyException.class);

        verify(restaurantPersistencePort, never()).findById(any());
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicatedOrderDishesException when items contain duplicate dishIds in create order")
    void shouldThrowDuplicatedOrderDishesExceptionWhenItemsContainDuplicateDishIdsInCreateOrder() {
        var duplicatedOrder = Order.builder()
                .restaurant(Restaurant.builder().id(RESTAURANT_ID).build())
                .items(List.of(
                        OrderDish.builder().dish(Dish.builder().id(DISH_1_ID).build()).quantity(2).build(),
                        OrderDish.builder().dish(Dish.builder().id(DISH_1_ID).build()).quantity(1).build()
                ))
                .build();

        assertThatThrownBy(() -> orderUseCase.createOrder(duplicatedOrder, CLIENT_ID))
                .isInstanceOf(DuplicatedOrderDishException.class);

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

    @Test
    @DisplayName("Should assign chef and transition to IN_PREPARATION when order is in PENDING in assign order")
    void shouldAssignChefAndTransitionToInPreparationWhenOrderIsInPendingInAssignOrder() {
        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.of(pendingOrder));
        when(orderPersistencePort.save(any(Order.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = orderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID);

        var captor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).save(captor.capture());
        var persisted = captor.getValue();

        assertThat(persisted.getStatus()).isEqualTo(OrderStatus.IN_PREPARATION);
        assertThat(persisted.getChefId()).isEqualTo(EMPLOYEE_ID);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_PREPARATION);
        assertThat(result.getChefId()).isEqualTo(EMPLOYEE_ID);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when the order does not exist in assign order")
    void shouldThrowOrderNotFoundExceptionWhenTheOrderDoesNotExistInAssignOrder() {
        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName(
            "Should throw EmployeeWithoutRestaurantException when " +
            "the employee has no restaurant assigned in assign order"
    )
    void shouldThrowEmployeeWithoutRestaurantExceptionWhenTheEmployeeHasNoRestaurantAssignedInAssignOrder() {
        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID))
                .isInstanceOf(EmployeeWithoutRestaurantException.class);

        verify(orderPersistencePort, never()).findById(any());
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName(
            "Should throw OrderOwnershipException when " +
            "the order does not belong to the employee's restaurant in assign order"
    )
    void shouldThrowOrderOwnershipExceptionWhenTheOrderDoesNotBelongToTheEmployeesRestaurantInAssignOrder() {
        var foreignOrder = Order.builder()
                .id(ORDER_ID)
                .restaurant(Restaurant.builder().id(99L).build())
                .status(OrderStatus.PENDING)
                .build();

        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.of(foreignOrder));

        assertThatThrownBy(() -> orderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID))
                .isInstanceOf(OrderEmployeeOwnershipException.class);

        verify(orderPersistencePort, never()).save(any());
    }

    @ParameterizedTest(name = "Should throw InvalidOrderStateException when the order is {0} in assign order")
    @EnumSource(
            value = OrderStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = { "PENDING" }
    )
    void shouldThrowInvalidOrderStateExceptionWhenTheOrderIsNotPendingInAssignOrder(OrderStatus invalidStatus) {
        pendingOrder.setStatus(invalidStatus);

        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.of(pendingOrder));

        assertThatThrownBy(() -> orderUseCase.assignOrder(ORDER_ID, EMPLOYEE_ID))
                .isInstanceOf(InvalidOrderStateException.class);

        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName(
            "Should generate a 6-digit PIN, set status to READY and notify customer " +
            "when order is IN_PREPARATION in mark order ready"
    )
    void shouldGenerateASixDigitPinSetStatusToReadyAndNotifyCustomerWhenOrderIsInPreparationInMarkOrderReady() {
        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.of(inPreparationOrder));
        when(userInformationPort.getUserById(CLIENT_ID))
                .thenReturn(customer);
        when(orderPersistencePort.save(any(Order.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = orderUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID);

        var captor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
        var persisted = captor.getValue();

        assertThat(persisted.getStatus()).isEqualTo(OrderStatus.READY);
        assertThat(persisted.getSecurityPin()).isNotNull();
        assertThat(persisted.getSecurityPin()).matches("\\d{6}");
        assertThat(result.getStatus()).isEqualTo(OrderStatus.READY);

        verify(notificationPort).notifyOrderReady(persisted, "+5198576854");
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when the order does not exist in mark order ready")
    void shouldThrowOrderNotFoundExceptionWhenTheOrderDoesNotExistInMarkOrderReady() {
        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderPersistencePort, never()).save(any());
        verify(notificationPort, never()).notifyOrderReady(any(), any());
    }

    @Test
    @DisplayName(
            "Should throw EmployeeWithoutRestaurantException when " +
            "the employee has no restaurant assigned in mark order ready"
    )
    void shouldThrowEmployeeWithoutRestaurantExceptionWhenTheEmployeeHasNoRestaurantAssignedInMarkOrderReady() {
        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID))
                .isInstanceOf(EmployeeWithoutRestaurantException.class);

        verify(orderPersistencePort, never()).findById(any());
    }

    @Test
    @DisplayName(
            "Should throw OrderEmployeeOwnershipException when " +
            "the order belongs to a different restaurant in mark order ready"
    )
    void shouldThrowOrderEmployeeOwnershipExceptionWhenTheOrderBelongsToADifferentRestaurantInMarkOrderReady() {
        var foreignOrder = Order.builder()
                .id(ORDER_ID)
                .restaurant(Restaurant.builder().id(99L).build())
                .status(OrderStatus.IN_PREPARATION)
                .build();

        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.of(foreignOrder));

        assertThatThrownBy(() -> orderUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID))
                .isInstanceOf(OrderEmployeeOwnershipException.class);

        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName(
            "Should throw OrderChefOwnershipException when " +
            "the employee is not the assigned chef in mark order ready"
    )
    void shouldThrowOrderChefOwnershipExceptionWhenTheEmployeeIsNotTheAssignedChefInMarkOrderReady() {
        inPreparationOrder.setChefId(99L);

        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.of(inPreparationOrder));

        assertThatThrownBy(() -> orderUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID))
                .isInstanceOf(OrderChefOwnershipException.class);

        verify(orderPersistencePort, never()).save(any());
        verify(notificationPort, never()).notifyOrderReady(any(), any());
    }

    @ParameterizedTest(name = "Should throw InvalidOrderStateException when the order is {0} in markOrderReady")
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = { "IN_PREPARATION" })
    void shouldThrowInvalidOrderStateExceptionWhenTheOrderIsNotInPreparationInMarkOrderReady(
            OrderStatus invalidStatus
    ) {
        inPreparationOrder.setStatus(invalidStatus);

        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.of(inPreparationOrder));

        assertThatThrownBy(() -> orderUseCase.markOrderReady(ORDER_ID, EMPLOYEE_ID))
                .isInstanceOf(InvalidOrderStateException.class);

        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName(
            "Should return paginated orders filtered by status when " +
            "employee is assigned to a restaurant in list orders by status"
    )
    void shouldReturnPaginatedOrdersFilteredByStatusWhenEmployeeIsAssignedToARestaurantInListOrdersByStatus() {
        var order1 = Order.builder().id(1L).status(OrderStatus.PENDING).build();
        var order2 = Order.builder().id(2L).status(OrderStatus.PENDING).build();
        var paged = PagedResult.of(List.of(order1, order2), 0, 10, 2L, 1);

        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findByRestaurantIdAndStatus(RESTAURANT_ID, OrderStatus.PENDING, 0, 10))
                .thenReturn(paged);

        var result = orderUseCase.listOrdersByStatus(OrderStatus.PENDING, EMPLOYEE_ID, 0, 10);

        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getTotalElements()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should return empty page when restaurant has no orders in the given status in list orders by status")
    void shouldReturnEmptyPageWhenRestaurantHasNoOrdersInTheGivenStatusInListOrdersByStatus() {
        var emptyPaged = PagedResult.<Order>of(List.of(), 0, 10, 0L, 0);

        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findByRestaurantIdAndStatus(RESTAURANT_ID, OrderStatus.READY, 0, 10))
                .thenReturn(emptyPaged);

        var result = orderUseCase.listOrdersByStatus(OrderStatus.READY, EMPLOYEE_ID, 0, 10);

        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName(
            "Should throw EmployeeWithoutRestaurantException when " +
            "employee has no restaurant assigned in list orders by status"
    )
    void shouldThrowEmployeeWithoutRestaurantExceptionWhenEmployeeHasNoRestaurantAssignedInListOrdersByStatus() {
        when(restaurantEmployeePersistencePort.findRestaurantIdByUserId(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderUseCase.listOrdersByStatus(OrderStatus.PENDING, EMPLOYEE_ID, 0, 10))
                .isInstanceOf(EmployeeWithoutRestaurantException.class);

        verify(orderPersistencePort, never()).findByRestaurantIdAndStatus(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should throw InvalidPaginationException when page is negative in list orders by status")
    void shouldThrowInvalidPaginationExceptionWhenPageIsNegativeInListOrdersByStatus() {
        assertThatThrownBy(() -> orderUseCase.listOrdersByStatus(OrderStatus.PENDING, EMPLOYEE_ID, -1, 10))
                .isInstanceOf(InvalidPaginationException.class);

        verify(restaurantEmployeePersistencePort, never()).findRestaurantIdByUserId(any());
        verify(orderPersistencePort, never()).findByRestaurantIdAndStatus(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should throw InvalidPaginationException when size exceeds max page size in list orders by status")
    void shouldThrowInvalidPaginationExceptionWhenSizeExceedsMaxPageSizeInListOrdersByStatus() {
        assertThatThrownBy(() -> orderUseCase.listOrdersByStatus(OrderStatus.PENDING, EMPLOYEE_ID, 0, 101))
                .isInstanceOf(InvalidPaginationException.class);

        verify(restaurantEmployeePersistencePort, never()).findRestaurantIdByUserId(any());
        verify(orderPersistencePort, never()).findByRestaurantIdAndStatus(any(), any(), anyInt(), anyInt());
    }
}
