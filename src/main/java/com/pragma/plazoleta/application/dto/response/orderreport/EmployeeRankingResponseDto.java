package com.pragma.plazoleta.application.dto.response.orderreport;

import com.pragma.plazoleta.application.dto.response.orderstate.OrderUserResponseDto;
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
public class EmployeeRankingResponseDto {

    private OrderUserResponseDto employee;

    @Schema(example = "12.5")
    private Double averageMinutes;

    @Schema(example = "18")
    private Long ordersHandled;
}
