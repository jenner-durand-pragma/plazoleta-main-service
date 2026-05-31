package com.pragma.plazoleta.application.dto.response.restaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantListItemResponseDto {

    @Schema(description = "Restaurant Id",
            example = "1")
    private Long id;

    @Schema(description = "Restaurant display name", example = "Pizza Place")
    private String name;

    @Schema(
            description = "Public URL of the restaurant logo",
            example = "https://logo.example.com/image.png"
    )
    private String logoUrl;
}
