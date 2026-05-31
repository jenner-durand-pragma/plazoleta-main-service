package com.pragma.plazoleta.application.dto.response.dish;

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
public class DishListItemResponseDto {

    @Schema(description = "Dish id", example = "1")
    private Long id;

    @Schema(description = "Dish name", example = "Pineapple Pizza")
    private String name;

    @Schema(
            description = "Dish description",
            example = "Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks"
    )
    private String description;

    @Schema(description = "Dish price", example = "15000")
    private Integer price;

    @Schema(description = "Public URL of the dish image", example = "https://dishes.example.com/dish.png")
    private String imageUrl;

    @Schema(description = "Dish category name", example = "Main Course")
    private String categoryName;
}
