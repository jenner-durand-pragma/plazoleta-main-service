package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.response.orderstate.OrderTraceabilityResponseDto;
import com.pragma.plazoleta.application.handler.IOrderTraceabilityHandler;
import com.pragma.plazoleta.application.mapper.IOrderTraceabilityResponseMapper;
import com.pragma.plazoleta.domain.api.IOrderTraceabilityServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderTraceabilityHandler implements IOrderTraceabilityHandler {

    private final IOrderTraceabilityServicePort orderTraceabilityServicePort;
    private final IOrderTraceabilityResponseMapper orderTraceabilityResponseMapper;

    @Override
    public OrderTraceabilityResponseDto findByOrderIdForClient(Long orderId, Long clientId) {
        var orderTraceability = orderTraceabilityServicePort.findByOrderIdForClient(orderId, clientId);

        return orderTraceabilityResponseMapper.toResponse(orderTraceability);
    }
}
