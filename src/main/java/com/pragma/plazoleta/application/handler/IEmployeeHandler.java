package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.employee.CreateEmployeeRequestDto;
import com.pragma.plazoleta.application.dto.response.employee.EmployeeAssignmentResponseDto;

public interface IEmployeeHandler {

    EmployeeAssignmentResponseDto createEmployee(
            Long restaurantId,
            CreateEmployeeRequestDto request,
            Long ownerId
    );

}
