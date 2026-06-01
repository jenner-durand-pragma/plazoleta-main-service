package com.pragma.plazoleta.infrastructure.out.jpa.repository;

import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.infrastructure.out.jpa.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {

    Page<OrderEntity> findByRestaurantIdAndStatus(
            Long restaurantId,
            OrderStatus status,
            Pageable pageable
    );
    Boolean existsByClientIdAndStatusIn(Long clientId, Collection<OrderStatus> statuses);
}
