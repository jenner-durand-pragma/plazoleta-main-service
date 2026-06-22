package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.OrderTraceability;

public interface IOrderTraceabilityServicePort {

    OrderTraceability findByOrderIdForClient(Long orderId, Long clientId);
}
