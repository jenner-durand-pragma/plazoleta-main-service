package com.pragma.plazoleta.domain.model;

import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.exception.order.DuplicatedOrderDishException;
import com.pragma.plazoleta.domain.exception.order.InvalidOrderStateException;
import com.pragma.plazoleta.domain.exception.order.OrderDishesEmptyException;
import com.pragma.plazoleta.domain.exception.order.OrderEmployeeOwnershipException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private Long id;
    private Long clientId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Long chefId;

    private Restaurant restaurant;
    private List<OrderDish> items;

    public void checkItemsNotEmpty() {
        if (items == null || items.isEmpty()) {
            throw new OrderDishesEmptyException();
        }
    }

    public void checkNotDuplicatedItems() {
        var dishIds = items.stream()
                .map(item -> item.getDish().getId())
                .collect(Collectors.toList());
        var dishIdsNotDuplicated = new HashSet<>(dishIds).size();

        if (dishIdsNotDuplicated != dishIds.size()) {
            throw new DuplicatedOrderDishException();
        }
    }

    public void checkEmployeeRestaurantBelongsToOrderRestaurant(Long restaurantId) {
        if (!restaurant.getId().equals(restaurantId)) {
            throw new OrderEmployeeOwnershipException();
        }
    }

    public void checkStatusIsPending() {
        if (status != OrderStatus.PENDING) {
            throw InvalidOrderStateException.orderNotPending(status.name());
        }
    }
}
