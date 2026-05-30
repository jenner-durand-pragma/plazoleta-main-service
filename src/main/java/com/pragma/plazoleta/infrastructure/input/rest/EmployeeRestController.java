package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.employee.CreateEmployeeRequestDto;
import com.pragma.plazoleta.application.dto.response.employee.EmployeeAssignmentResponseDto;
import com.pragma.plazoleta.application.handler.IEmployeeHandler;
import com.pragma.plazoleta.infrastructure.configuration.security.annotation.IsOwner;
import com.pragma.plazoleta.infrastructure.configuration.security.token.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Restaurant employee management operations")
public class EmployeeRestController {

    private final IEmployeeHandler employeeHandler;

    @IsOwner
    @PostMapping
    public ResponseEntity<EmployeeAssignmentResponseDto> createEmployee(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateEmployeeRequestDto request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return null;
    }
}
