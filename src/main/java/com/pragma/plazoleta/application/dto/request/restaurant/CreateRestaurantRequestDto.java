package com.pragma.plazoleta.application.dto.request.restaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRestaurantRequestDto {

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^(?=.*\\p{L})[\\p{L}0-9\\s]+$", message = "Restaurant name cannot contain only numbers")
    @Schema(
            description = "Restaurant display name",
            example = "Pizza Place"
    )
    private String name;

    @NotBlank(message = "NIT is required")
    @Pattern(regexp = "^\\d+$", message = "NIT must be numeric only")
    @Schema(
            description = "Numeric tax identifier (NIT)",
            example = "9001234567"
    )
    private String nit;

    @NotBlank(message = "Address is required")
    @Schema(
            description = "Physical address of the restaurant",
            example = "Example Street 123"
    )
    private String address;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?\\d{1,13}$",
            message = "Phone must be numeric, max 13 characters, optional leading '+'")
    @Schema(
            description = "Contact phone. Up to 13 digits with optional leading '+'.",
            example = "+573005698325"
    )
    private String phone;

    @NotBlank(message = "Logo URL is required")
    @Schema(
            description = "Public URL of the restaurant logo",
            example = "https://logo.example.com/image.png"
    )
    private String logoUrl;

    @NotNull(message = "Owner id is required")
    @Positive(message = "Owner id must be positive")
    @Schema(
            description = "Id of an existing user with role OWNER",
            example = "2"
    )
    private Long ownerId;

}
