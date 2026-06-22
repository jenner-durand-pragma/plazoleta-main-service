package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.exception.order.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.order.InvalidOrderDishesException;
import com.pragma.plazoleta.domain.exception.order.OrderCannotBeCancelledException;
import com.pragma.plazoleta.domain.exception.order.OrderNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurantemployee.EmployeeWithoutRestaurantException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderState;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.INotificationPort;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.IOrderTraceabilityPort;
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
    private final IOrderTraceabilityPort orderTraceabilityPort;

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

        var savedOrder = orderPersistencePort.save(newOrder);

        captureState(savedOrder, null, null);

        return savedOrder;
    }

    @Override
    public Order assignOrder(Long orderId, Long employeeId) {
        var employeeRestaurantId = resolveEmployeeRestaurant(employeeId);
        var order = resolveOrder(orderId);

        order.checkEmployeeRestaurantBelongsToOrderRestaurant(employeeRestaurantId);
        order.checkStatusIsPending();

        var previousStatus = order.getStatus();

        order.setChefId(employeeId);
        order.setStatus(OrderStatus.IN_PREPARATION);

        var savedOrder = orderPersistencePort.save(order);

        captureState(savedOrder, previousStatus, employeeId);

        return savedOrder;
    }

    @Override
    public Order markOrderReady(Long orderId, Long employeeId) {
        var employeeRestaurantId = resolveEmployeeRestaurant(employeeId);
        var order = resolveOrder(orderId);

        order.checkEmployeeRestaurantBelongsToOrderRestaurant(employeeRestaurantId);
        order.checkEmployeeIsAssignedChef(employeeId);
        order.checkStatusIsInPreparation();

        var previousStatus = order.getStatus();

        order.generateSixDigitPin();
        order.setStatus(OrderStatus.READY);
        var savedOrder = orderPersistencePort.save(order);

        var customer = userInformationPort.getUserById(savedOrder.getClientId());
        notificationPort.notifyOrderReady(savedOrder, customer.getPhone());

        captureState(savedOrder, previousStatus, employeeId);

        return savedOrder;
    }

    @Override
    public Order markOrderDelivered(Long orderId, Long employeeId, String securityPin) {
        var employeeRestaurantId = resolveEmployeeRestaurant(employeeId);
        var order = resolveOrder(orderId);

        order.checkEmployeeRestaurantBelongsToOrderRestaurant(employeeRestaurantId);
        order.checkStatusIsReady();
        order.checkSecurityPin(securityPin);

        var previousStatus = order.getStatus();

        order.setStatus(OrderStatus.DELIVERED);

        var savedOrder = orderPersistencePort.save(order);

        captureState(savedOrder, previousStatus, employeeId);

        return savedOrder;
    }

    @Override
    public Order cancelOrder(Long orderId, Long clientId) {
        var order = resolveOrder(orderId);

        order.checkBelongsToClient(clientId);
        ensureOrderIsPendingToCancel(order);

        var previousStatus = order.getStatus();

        order.setStatus(OrderStatus.CANCELLED);

        var savedOrder = orderPersistencePort.save(order);

        captureState(savedOrder, previousStatus, null);

        return savedOrder;
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

    private void ensureOrderIsPendingToCancel(Order order) {
        if (Boolean.FALSE.equals(order.isStatus(OrderStatus.PENDING))) {
            var client = userInformationPort.getUserById(order.getClientId());
            notificationPort.notifyOrderCannotCancelled(order, client.getPhone());

            throw new OrderCannotBeCancelledException();
        }
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

    private void captureState(Order order, OrderStatus previousStatus, Long employeeId) {
        var orderState = OrderState.builder()
                .orderId(order.getId())
                .clientId(order.getClientId())
                .restaurantId(order.getRestaurant().getId())
                .previousStatus(previousStatus)
                .newStatus(order.getStatus())
                .employeeId(employeeId)
                .changedAt(LocalDateTime.now())
                .build();

        orderTraceabilityPort.saveState(orderState);
    }
}
