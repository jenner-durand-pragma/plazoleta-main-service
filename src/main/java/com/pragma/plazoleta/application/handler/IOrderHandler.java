package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.order.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.response.order.OrderResponseDto;

public interface IOrderHandler {

    OrderResponseDto createOrder(Long restaurantId, CreateOrderRequestDto request, Long clientId);

}
