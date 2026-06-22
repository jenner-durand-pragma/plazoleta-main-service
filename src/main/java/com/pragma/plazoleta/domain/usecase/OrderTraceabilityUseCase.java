package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IOrderTraceabilityServicePort;
import com.pragma.plazoleta.domain.model.OrderTraceability;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.IOrderTraceabilityPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderTraceabilityUseCase implements IOrderTraceabilityServicePort {

    private final IOrderTraceabilityPort orderTraceabilityPort;
    private final IOrderPersistencePort orderPersistencePort;

    @Override
    public OrderTraceability findByOrderIdForClient(Long orderId, Long clientId) {
        return null;
    }
}
