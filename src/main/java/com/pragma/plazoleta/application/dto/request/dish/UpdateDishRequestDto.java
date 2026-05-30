package com.pragma.plazoleta.application.dto.request.dish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDishRequestDto {

    @Positive(message = "Price must be a positive integer greater than 0")
    @Schema(description = "New price as a positive integer", example = "20000")
    private Integer price;

    @Schema(description = "New description", example = "Updated tomato sauce, mozzarella and basil")
    private String description;
}
