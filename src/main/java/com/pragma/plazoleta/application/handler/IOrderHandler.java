package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.order.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.order.DeliverOrderRequestDto;
import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.order.OrderResponseDto;
import com.pragma.plazoleta.domain.enums.OrderStatus;

public interface IOrderHandler {

    OrderResponseDto createOrder(Long restaurantId, CreateOrderRequestDto request, Long clientId);
    OrderResponseDto assignOrder(Long orderId, Long employeeId);
    OrderResponseDto markOrderReady(Long orderId, Long employeeId);
    OrderResponseDto markOrderDelivered(Long orderId, Long employeeId, DeliverOrderRequestDto request);

    PagedResponseDto<OrderResponseDto> listOrdersByStatus(
            OrderStatus status,
            Long employeeId,
            Integer page,
            Integer size
    );
}
