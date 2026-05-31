package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public Order save(Order order) {
        var entity = orderEntityMapper.toEntity(order);
        var saved = orderRepository.save(entity);

        return orderEntityMapper.toModel(saved);
    }

    @Override
    public Boolean existsActiveOrderByClientId(Long clientId) {
        return orderRepository.existsByClientIdAndStatusIn(
                clientId,
                OrderStatus.ACTIVE_STATUSES
        );
    }
}
