package com.pragma.plazoleta.infrastructure.out.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTraceabilityResponseDto {

    private Long orderId;
    private OrderStateUserInformationDto client;
    private List<OrderStateTraceabilityResponseDto> transitions;
}
