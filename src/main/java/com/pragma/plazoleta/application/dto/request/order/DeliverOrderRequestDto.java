package com.pragma.plazoleta.application.dto.request.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliverOrderRequestDto {

    @NotBlank(message = "Security PIN is required")
    @Pattern(regexp = "^\\d{6}$", message = "Security PIN must be 6 digits")
    @Schema(description = "6-digit security PIN", example = "482910")
    private String securityPin;
}
