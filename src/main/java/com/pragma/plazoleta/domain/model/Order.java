package com.pragma.plazoleta.domain.model;

import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.exception.order.DuplicatedOrderDishException;
import com.pragma.plazoleta.domain.exception.order.InvalidOrderStateException;
import com.pragma.plazoleta.domain.exception.order.InvalidSecurityPinException;
import com.pragma.plazoleta.domain.exception.order.OrderChefOwnershipException;
import com.pragma.plazoleta.domain.exception.order.OrderDishesEmptyException;
import com.pragma.plazoleta.domain.exception.order.OrderEmployeeOwnershipException;
import com.pragma.plazoleta.domain.exception.order.OrderNotBelongsToClientException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    private String securityPin;

    private Restaurant restaurant;
    private List<OrderDish> items;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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

    public void checkEmployeeIsAssignedChef(Long employeeId) {
        if (chefId == null || !chefId.equals(employeeId)) {
            throw new OrderChefOwnershipException();
        }
    }

    public Boolean isStatus(OrderStatus orderStatus) {
        return Objects.equals(orderStatus, this.status);
    }

    public void checkStatusIsPending() {
        if (Boolean.FALSE.equals(isStatus(OrderStatus.PENDING))) {
            throw InvalidOrderStateException.orderNotPending(status.name());
        }
    }

    public void checkStatusIsInPreparation() {
        if (Boolean.FALSE.equals(isStatus(OrderStatus.IN_PREPARATION))) {
            throw InvalidOrderStateException.orderNotInPreparation(status.name());
        }
    }

    public void checkStatusIsReady() {
        if (Boolean.FALSE.equals(isStatus(OrderStatus.READY))) {
            throw InvalidOrderStateException.orderNotReady(status.name());
        }
    }

    public void checkSecurityPin(String providedSecurityPin) {
        if (securityPin == null || !securityPin.equals(providedSecurityPin)) {
            throw new InvalidSecurityPinException();
        }
    }

    public void generateSixDigitPin() {
        var pin = SECURE_RANDOM.nextInt(900000) + 100000;

        securityPin = String.valueOf(pin);
    }

    public void checkBelongsToClient(Long clientId) {
        if (!Objects.equals(clientId, this.clientId)) {
            throw new OrderNotBelongsToClientException(id);
        }
    }
}
