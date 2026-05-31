package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Order;

public interface IOrderServicePort {

    Order createOrder(Order order, Long clientId);

}
