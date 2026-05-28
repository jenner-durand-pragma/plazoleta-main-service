package com.pragma.plazoleta.application.dto.response.restaurant;

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
public class RestaurantResponseDto {

    @Schema(description = "Generated restaurant id", example = "1")
    private Long id;

    @Schema(description = "Restaurant display name", example = "Pizza Place")
    private String name;

    @Schema(description = "Physical address", example = "Example Street 123")
    private String address;

    @Schema(description = "Id of the owner user", example = "2")
    private Long ownerId;

    @Schema(description = "Contact phone", example = "+573005698325")
    private String phone;

    @Schema(description = "Public URL of the restaurant logo", example = "https://logo.example.com/image.png")
    private String logoUrl;

    @Schema(description = "Numeric tax identifier", example = "9001234567")
    private String nit;

}
