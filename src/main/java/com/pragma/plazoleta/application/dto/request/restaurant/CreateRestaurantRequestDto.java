package com.pragma.plazoleta.application.dto.request.restaurant;

import lombok.*;

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
    @Pattern(regexp = "^(?=.*\\p{L})[\\p{L}0-91\\s]+$", message = "Restaurant name cannot contain only numbers")
    private String name;

    @NotBlank(message = "NIT is required")
    @Pattern(regexp = "^\\d+$", message = "NIT must be numeric only")
    private String nit;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?\\d{1,13}$",
            message = "Phone must be numeric, max 13 characters, optional leading '+'")
    private String phone;

    @NotBlank(message = "Logo URL is required")
    private String logoUrl;

    @NotNull(message = "Owner id is required")
    @Positive(message = "Owner id must be positive")
    private Long ownerId;

}
