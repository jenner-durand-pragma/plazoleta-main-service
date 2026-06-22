package com.pragma.plazoleta.application.dto.response.orderstate;

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
public class OrderUserTraceabilityResponseDto {

    @Schema(description = "User id", example = "10")
    private Long id;

    @Schema(description = "User first name", example = "Jenner")
    private String name;

    @Schema(description = "User last name", example = "Durand")
    private String lastName;

    @Schema(description = "User email", example = "jenner.durand@plazoleta.com")
    private String email;
}
