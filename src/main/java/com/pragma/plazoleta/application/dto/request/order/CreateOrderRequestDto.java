package com.pragma.plazoleta.application.dto.request.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequestDto {

    @Valid
    @NotEmpty(message = "Items are required and must contain at least one entry")
    @Schema(description = "Dishes ordered with their quantities")
    private List<CreateOrderDishDto> items;
}
