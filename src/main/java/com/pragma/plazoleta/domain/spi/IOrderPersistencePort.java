package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.model.Order;

public interface IOrderPersistencePort {

    Order save(Order order);

    PagedResult<Order> findByRestaurantIdAndStatus(
            Long restaurantId,
            OrderStatus status,
            Integer page,
            Integer size
    );
    Boolean existsActiveOrderByClientId(Long clientId);
}
