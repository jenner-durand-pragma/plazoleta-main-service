package com.pragma.plazoleta.infrastructure.out.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEfficiencyFeignResponseDto {

    private Long orderId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Double durationMinutes;
    private OrderStateUserInformationDto client;
}
