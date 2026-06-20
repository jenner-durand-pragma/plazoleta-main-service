package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Order;

public interface INotificationPort {

    void notifyOrderReady(Order order, String clientPhone);
    void notifyOrderCannotCancelled(Order order, String clientPhone);

}
