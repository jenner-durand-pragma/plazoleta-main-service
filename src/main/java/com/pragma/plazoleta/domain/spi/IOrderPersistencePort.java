package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Order;

public interface IOrderPersistencePort {

    Order save(Order order);

    Boolean existsActiveOrderByClientId(Long clientId);
}
