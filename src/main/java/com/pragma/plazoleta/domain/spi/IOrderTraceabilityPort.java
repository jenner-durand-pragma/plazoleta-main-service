package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.OrderState;
import com.pragma.plazoleta.domain.model.OrderTraceability;

public interface IOrderTraceabilityPort {

    void saveState(OrderState orderState);
    OrderTraceability findByOrderId(Long orderId);
}
