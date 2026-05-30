package com.pragma.plazoleta.application.dto.response.employee;

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
public class EmployeeAssignmentResponseDto {

    @Schema(description = "Id of the user created in users-service", example = "99")
    private Long userId;

    @Schema(description = "Id of the restaurant the employee belongs to", example = "10")
    private Long restaurantId;
}
