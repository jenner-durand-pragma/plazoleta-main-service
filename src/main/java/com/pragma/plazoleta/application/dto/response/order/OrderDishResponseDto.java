package com.pragma.plazoleta.application.dto.response.order;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class OrderDishResponseDto {

    @Schema(description = "Dish id", example = "1")
    private Long dishId;

    @Schema(description = "Dish name", example = "Pineapple Pizza")
    private String dishName;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;
}
