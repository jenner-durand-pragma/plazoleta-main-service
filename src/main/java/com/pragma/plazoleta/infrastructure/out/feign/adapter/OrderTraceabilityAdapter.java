package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.pragma.plazoleta.domain.model.OrderState;
import com.pragma.plazoleta.domain.model.OrderTraceability;
import com.pragma.plazoleta.domain.spi.IOrderTraceabilityPort;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import com.pragma.plazoleta.infrastructure.out.feign.client.IOrderTraceabilityFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderStateRequestDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderStateUserInformationDto;
import com.pragma.plazoleta.infrastructure.out.feign.mapper.IOrderTraceabilityFeignMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OrderTraceabilityAdapter implements IOrderTraceabilityPort {

    private final IOrderTraceabilityFeignClient orderTraceabilityFeignClient;
    private final IUserInformationPort userInformationPort;
    private final IOrderTraceabilityFeignMapper orderTraceabilityFeignMapper;

    @Override
    public void saveState(OrderState orderState) {
        try {
            var client = resolverUser(orderState.getClientId());
            var employee = orderState.getEmployeeId() == null
                    ? null
                    : resolverUser(orderState.getEmployeeId());

            var request = OrderStateRequestDto.builder()
                    .orderId(orderState.getOrderId())
                    .restaurantId(orderState.getRestaurantId())
                    .previousStatus(orderState.getPreviousStatus())
                    .newStatus(orderState.getNewStatus())
                    .changedAt(orderState.getChangedAt())
                    .client(client)
                    .employee(employee)
                    .build();

            orderTraceabilityFeignClient.saveState(request);

            log.debug("Traceability captured for order #{}: {} -> {}",
                    orderState.getOrderId(),
                    orderState.getPreviousStatus(),
                    orderState.getNewStatus());
        } catch (FeignException ex) {

            log.warn("Could not record traceability for order #{} ({} -> {}). Status: {}.",
                    orderState.getOrderId(),
                    orderState.getPreviousStatus(),
                    orderState.getNewStatus(),
                    ex.status(),
                    ex
            );

            throw ex;
        }
    }

    @Override
    public OrderTraceability findByOrderId(Long orderId) {
        try {
            var orderTraceabilityResponse = orderTraceabilityFeignClient.findByOrderId(orderId);

            return orderTraceabilityFeignMapper.toModel(orderTraceabilityResponse);
        } catch (FeignException.NotFound e) {

            return null;
        }
    }

    private OrderStateUserInformationDto resolverUser(Long userId) {
        var user = userInformationPort.getUserById(userId);

        return OrderStateUserInformationDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
