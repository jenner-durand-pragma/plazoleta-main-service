package com.pragma.plazoleta.application.dto.request.dish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDishStatusRequestDto {

    @NotNull(message = "Active flag is required")
    @Schema(
            description = "Whether the dish is active (true) or hidden from the menu (false)",
            example = "false"
    )
    private Boolean active;

}
