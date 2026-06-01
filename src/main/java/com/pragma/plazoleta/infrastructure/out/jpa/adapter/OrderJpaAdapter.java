package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IOrderRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;
    private final IRestaurantRepository restaurantRepository;
    private final IDishRepository dishRepository;

    @Override
    public Order save(Order order) {
        var entity = orderEntityMapper.toEntity(order);

        entity.setRestaurant(restaurantRepository.getReferenceById(entity.getRestaurant().getId()));
        if (entity.getOrderDishes() != null) {
            entity.getOrderDishes().forEach(od ->
                    od.setDish(dishRepository.getReferenceById(od.getDish().getId()))
            );
        }

        var saved = orderRepository.save(entity);

        return orderEntityMapper.toModel(saved);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return Optional.empty();
    }

    @Override
    public PagedResult<Order> findByRestaurantIdAndStatus(
            Long restaurantId,
            OrderStatus status,
            Integer page,
            Integer size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "orderDate"));
        var orderPage = orderRepository.findByRestaurantIdAndStatus(
                restaurantId,
                status,
                pageable
        );

        var items = orderPage.getContent().stream()
                .map(orderEntityMapper::toModel)
                .collect(Collectors.toList());

        return PagedResult.of(
                items,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
    }

    @Override
    public Boolean existsActiveOrderByClientId(Long clientId) {
        return orderRepository.existsByClientIdAndStatusIn(
                clientId,
                OrderStatus.ACTIVE_STATUSES
        );
    }
}
