package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.exception.order.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.order.InvalidOrderDishesException;
import com.pragma.plazoleta.domain.exception.order.OrderNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurantemployee.EmployeeWithoutRestaurantException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.INotificationPort;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    private final INotificationPort notificationPort;
    private final IUserInformationPort userInformationPort;

    @Override
    public Order createOrder(Order order, Long clientId) {
        order.checkItemsNotEmpty();
        order.checkNotDuplicatedItems();

        var restaurantId = order.getRestaurant().getId();
        var restaurant = resolveRestaurant(restaurantId);

        ensureClientHasNoActiveOrder(clientId);

        var resolvedItems = resolveAndValidateDishes(order.getItems(), restaurantId);

        var newOrder = Order.builder()
                .clientId(clientId)
                .restaurant(restaurant)
                .items(resolvedItems)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .chefId(null)
                .build();

        return orderPersistencePort.save(newOrder);
    }

    @Override
    public Order assignOrder(Long orderId, Long employeeId) {
        var employeeRestaurantId = resolveEmployeeRestaurant(employeeId);
        var order = resolveOrder(orderId);

        order.checkEmployeeRestaurantBelongsToOrderRestaurant(employeeRestaurantId);
        order.checkStatusIsPending();

        order.setChefId(employeeId);
        order.setStatus(OrderStatus.IN_PREPARATION);

        return orderPersistencePort.save(order);
    }

    @Override
    public Order markOrderReady(Long orderId, Long employeeId) {
        return null;
    }

    @Override
    public PagedResult<Order> listOrdersByStatus(
            OrderStatus status,
            Long employeeId,
            Integer page,
            Integer size
    ) {
        PagedResult.validatePagination(page, size);

        var restaurantId = restaurantEmployeePersistencePort
                .findRestaurantIdByUserId(employeeId)
                .orElseThrow(EmployeeWithoutRestaurantException::new);

        return orderPersistencePort.findByRestaurantIdAndStatus(restaurantId, status, page, size);
    }

    private Restaurant resolveRestaurant(Long restaurantId) {
        var restaurant = restaurantPersistencePort.findById(restaurantId);
        if (restaurant == null) {
            throw new RestaurantNotFoundException(restaurantId);
        }

        return restaurant;
    }

    private Long resolveEmployeeRestaurant(Long employeeId) {
        return restaurantEmployeePersistencePort
                .findRestaurantIdByUserId(employeeId)
                .orElseThrow(EmployeeWithoutRestaurantException::new);
    }

    private Order resolveOrder(Long orderId) {
        return orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private void ensureClientHasNoActiveOrder(Long clientId) {
        var hasActiveOrder = orderPersistencePort.existsActiveOrderByClientId(clientId);

        if (Boolean.TRUE.equals(hasActiveOrder)) {
            throw new ClientHasActiveOrderException();
        }
    }

    private List<OrderDish> resolveAndValidateDishes(
            List<OrderDish> requestedItems,
            Long restaurantId
    ) {
        var requestedIds = requestedItems.stream()
                .map(item -> item.getDish().getId())
                .collect(Collectors.toList());

        var foundDishes = dishPersistencePort.findAllByIdIn(requestedIds);
        if (foundDishes.size() != requestedIds.size()) {
            throw InvalidOrderDishesException.notFound();
        }

        var dishesById = foundDishes.stream()
                .collect(Collectors.toMap(Dish::getId, dish -> dish));
        for (var dish : foundDishes) {
            if (Boolean.FALSE.equals(dish.getActive())) {
                throw InvalidOrderDishesException.notActive();
            }

            if (!dish.getRestaurant().getId().equals(restaurantId)) {
                throw InvalidOrderDishesException.notFromRestaurant();
            }
        }

        return requestedItems.stream()
                .map(item -> {
                    var dish = dishesById.get(item.getDish().getId());

                    return OrderDish.builder()
                            .dishId(dish.getId())
                            .dish(dish)
                            .quantity(item.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
