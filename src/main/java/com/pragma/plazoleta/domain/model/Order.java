package com.pragma.plazoleta.domain.model;

import com.pragma.plazoleta.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private Long id;
    private Long clientId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Long chefId;

    private Restaurant restaurant;
    private List<OrderItem> items;
}
