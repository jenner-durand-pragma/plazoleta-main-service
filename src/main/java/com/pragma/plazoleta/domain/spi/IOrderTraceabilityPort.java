package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.OrderState;

import java.util.List;

public interface IOrderTraceabilityPort {

    void saveState(OrderState orderState);
}
