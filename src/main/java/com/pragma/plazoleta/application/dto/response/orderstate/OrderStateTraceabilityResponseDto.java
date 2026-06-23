package com.pragma.plazoleta.application.dto.response.orderstate;

import com.pragma.plazoleta.domain.enums.OrderStatus;
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
public class OrderStateTraceabilityResponseDto {

    @Schema(
            description = "Previous order status, null when the transition is the initial creation",
            example = "IN_PREPARATION"
    )
    private OrderStatus previousStatus;

    @Schema(description = "New order status", example = "READY")
    private OrderStatus newStatus;

    @Schema(description = "Datetime when changed occurs", example = "2026-06-21T10:39:46")
    private LocalDateTime changedAt;

    @Schema(description = "Employee information")
    private OrderUserResponseDto employee;
}
