package com.pragma.plazoleta.infrastructure.out.feign.dto;

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
public class EmployeeEfficiencyFeignResponseDto {

    private OrderStateUserInformationDto employee;
    private Double averageMinutes;
    private Long ordersHandled;
}
