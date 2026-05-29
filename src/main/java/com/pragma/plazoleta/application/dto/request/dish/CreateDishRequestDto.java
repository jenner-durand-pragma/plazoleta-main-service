package com.pragma.plazoleta.application.dto.request.dish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDishRequestDto {

    @NotBlank(message = "Name is required")
    @Schema(description = "Dish display name", example = "Pineapple Pizza")
    private String name;

    @NotBlank(message = "Description is required")
    @Schema(
            description = "Dish description",
            example = "Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks"
    )
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive integer greater than 0")
    @Schema(description = "Price as a positive integer", example = "15000")
    private Integer price;

    @NotBlank(message = "Image URL is required")
    @Schema(description = "Public URL of the dish image", example = "https://dishes.example.com/dish.png")
    private String imageUrl;

    @NotNull(message = "Category id is required")
    @Positive(message = "Category id must be positive")
    @Schema(description = "Id of an existing category", example = "1")
    private Long categoryId;

    @NotNull(message = "Restaurant id is required")
    @Positive(message = "Restaurant id must be positive")
    @Schema(description = "Id of the restaurant where the dish will be served", example = "10")
    private Long restaurantId;

    @NotNull(message = "Owner id is required")
    @Positive(message = "Owner id must be positive")
    @Schema(
            description = "Id of the requesting owner",
            example = "2"
    )
    private Long ownerId;
}
