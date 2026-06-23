package com.pragma.plazoleta.application.dto.response.orderstate;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Order id", example = "42")
    private Long orderId;

    private OrderUserResponseDto client;

    private List<OrderStateTraceabilityResponseDto> transitions;
}
