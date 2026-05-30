package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.employee.EmployeeAssignmentResponseDto;
import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IEmployeeResponseMapper {

    EmployeeAssignmentResponseDto toResponse(RestaurantEmployee employee);

}
