package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.employee.CreateEmployeeRequestDto;
import com.pragma.plazoleta.application.dto.response.employee.EmployeeAssignmentResponseDto;
import com.pragma.plazoleta.application.handler.IEmployeeHandler;
import com.pragma.plazoleta.application.mapper.IEmployeeRequestMapper;
import com.pragma.plazoleta.application.mapper.IEmployeeResponseMapper;
import com.pragma.plazoleta.domain.api.IEmployeeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeHandler implements IEmployeeHandler {

    private final IEmployeeServicePort employeeServicePort;
    private final IEmployeeRequestMapper requestMapper;
    private final IEmployeeResponseMapper responseMapper;

    @Override
    public EmployeeAssignmentResponseDto createEmployee(
            Long restaurantId,
            CreateEmployeeRequestDto request,
            Long ownerId
    ) {
        var registration = requestMapper.toUserInformation(request);
        var created = employeeServicePort.createEmployee(restaurantId, registration, ownerId);

        return responseMapper.toResponse(created);
    }
}
