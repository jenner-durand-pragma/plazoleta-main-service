package com.pragma.plazoleta.application.dto.response.order;

import com.pragma.plazoleta.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class OrderResponseDto {

    @Schema(description = "Generated order id", example = "42")
    private Long id;

    @Schema(description = "Restaurant id", example = "10")
    private Long restaurantId;

    @Schema(description = "Id of the client who placed the order", example = "5")
    private Long clientId;

    @Schema(description = "When the order was placed", example = "2026-05-31T14:23:00")
    private LocalDateTime orderDate;

    @Schema(description = "Current status of the order", example = "PENDING")
    private OrderStatus status;

    @Schema(description = "Ordered dishes with quantities")
    private List<OrderDishResponseDto> items;
}
