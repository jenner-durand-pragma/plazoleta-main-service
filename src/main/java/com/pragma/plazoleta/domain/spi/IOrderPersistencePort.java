package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.model.Order;

import java.util.Optional;

public interface IOrderPersistencePort {

    Order save(Order order);

    Optional<Order> findById(Long orderId);
    PagedResult<Order> findByRestaurantIdAndStatus(
            Long restaurantId,
            OrderStatus status,
            Integer page,
            Integer size
    );
    Boolean existsActiveOrderByClientId(Long clientId);
}
