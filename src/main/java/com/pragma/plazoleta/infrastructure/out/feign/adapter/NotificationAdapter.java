package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.spi.INotificationPort;
import com.pragma.plazoleta.infrastructure.out.feign.client.INotificationFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NotificationAdapter implements INotificationPort {

    private final INotificationFeignClient notificationFeignClient;

    @Override
    public void notifyOrderReady(Order order, String customerPhone) {
        return;
    }
}
