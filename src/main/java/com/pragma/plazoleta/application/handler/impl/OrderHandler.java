package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.order.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.request.order.DeliverOrderRequestDto;
import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.order.OrderResponseDto;
import com.pragma.plazoleta.application.handler.IOrderHandler;
import com.pragma.plazoleta.application.mapper.IOrderRequestMapper;
import com.pragma.plazoleta.application.mapper.IOrderResponseMapper;
import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IOrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;

    @Override
    @Transactional
    public OrderResponseDto createOrder(
            Long restaurantId,
            CreateOrderRequestDto request,
            Long clientId
    ) {
        var domainOrder = orderRequestMapper.toOrder(request, restaurantId);
        var created = orderServicePort.createOrder(domainOrder, clientId);

        return orderResponseMapper.toResponse(created);
    }

    @Override
    @Transactional
    public OrderResponseDto assignOrder(Long orderId, Long employeeId) {
        var assigned = orderServicePort.assignOrder(orderId, employeeId);

        return orderResponseMapper.toResponse(assigned);
    }

    @Override
    @Transactional
    public OrderResponseDto markOrderReady(Long orderId, Long employeeId) {
        var orderReady = orderServicePort.markOrderReady(orderId, employeeId);

        return orderResponseMapper.toResponse(orderReady);
    }

    @Override
    public OrderResponseDto markOrderDelivered(Long orderId, Long employeeId, DeliverOrderRequestDto request) {
        return null;
    }

    @Override
    public PagedResponseDto<OrderResponseDto> listOrdersByStatus(
            OrderStatus status,
            Long employeeId,
            Integer page,
            Integer size
    ) {
        var paged = orderServicePort.listOrdersByStatus(status, employeeId, page, size);
        var pagedMapped = paged.mapTo(orderResponseMapper::toResponse);

        return PagedResponseDto.from(pagedMapped);
    }
}
