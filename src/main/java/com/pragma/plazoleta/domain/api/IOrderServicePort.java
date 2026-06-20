package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.model.Order;

public interface IOrderServicePort {

    Order createOrder(Order order, Long clientId);
    Order assignOrder(Long orderId, Long employeeId);
    Order markOrderReady(Long orderId, Long employeeId);
    Order markOrderDelivered(Long orderId, Long employeeId, String securityPin);

    PagedResult<Order> listOrdersByStatus(
            OrderStatus status,
            Long employeeId,
            Integer page,
            Integer size
    );
}
