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
public class DishResponseDto {

    @Schema(description = "Generated dish id", example = "1")
    private Long id;

    @Schema(description = "Dish display name", example = "Pineapple Pizza")
    private String name;

    @Schema(description = "Dish description", example = "Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
    private String description;

    @Schema(description = "Price as a positive integer", example = "15000")
    private Integer price;

    @Schema(description = "Public URL of the dish image", example = "https://dishes.example.com/dish.png")
    private String imageUrl;

    @Schema(description = "Category name", example = "Main Course")
    private String categoryName;

    @Schema(description = "Restaurant id", example = "10")
    private Long restaurantId;

    @Schema(description = "Whether the dish is active", example = "true")
    private Boolean active;
}
