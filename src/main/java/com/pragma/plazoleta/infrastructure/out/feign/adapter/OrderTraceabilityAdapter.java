package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.pragma.plazoleta.domain.model.OrderState;
import com.pragma.plazoleta.domain.spi.IOrderTraceabilityPort;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import com.pragma.plazoleta.infrastructure.out.feign.client.IOrderTraceabilityFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OrderTraceabilityAdapter implements IOrderTraceabilityPort {

    private final IOrderTraceabilityFeignClient orderTraceabilityFeignClient;
    private final IUserInformationPort userInformationPort;

    @Override
    public void saveState(OrderState orderState) {
        return;
    }
}
