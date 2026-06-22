package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.response.orderstate.OrderTraceabilityResponseDto;

public interface IOrderTraceabilityHandler {
    OrderTraceabilityResponseDto findByOrderIdForClient(Long orderId, Long clientId);
}
