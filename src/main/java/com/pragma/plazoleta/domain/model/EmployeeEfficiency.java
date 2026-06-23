package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEfficiency {

    private OrderUser employee;
    private Double averageMinutes;
    private Long ordersHandled;
}
