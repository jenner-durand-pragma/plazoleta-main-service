package com.pragma.plazoleta.domain.model;

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
public class OrderEfficiency {

    private Long orderId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Double durationMinutes;

    private OrderUser client;
}
