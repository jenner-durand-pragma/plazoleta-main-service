package com.pragma.plazoleta.application.dto.response.orderreport;

import com.pragma.plazoleta.application.dto.response.orderstate.OrderUserResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class OrderEfficiencyResponseDto {

    @Schema(example = "42")
    private Long orderId;

    @Schema(example = "2026-05-31T15:00:12")
    private LocalDateTime startedAt;

    @Schema(example = "2026-05-31T15:35:20")
    private LocalDateTime endedAt;

    @Schema(example = "35.1")
    private Double durationMinutes;

    private OrderUserResponseDto client;
}
