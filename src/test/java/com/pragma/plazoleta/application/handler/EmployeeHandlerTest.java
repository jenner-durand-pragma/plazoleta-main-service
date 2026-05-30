package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.employee.CreateEmployeeRequestDto;
import com.pragma.plazoleta.application.handler.impl.EmployeeHandler;
import com.pragma.plazoleta.application.mapper.IEmployeeRequestMapper;
import com.pragma.plazoleta.application.mapper.IEmployeeResponseMapper;
import com.pragma.plazoleta.domain.api.IEmployeeServicePort;
import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.model.UserInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeHandlerTest {

    @Mock
    private IEmployeeServicePort employeeServicePort;

    @Spy
    private IEmployeeRequestMapper requestRequestMapper = Mappers.getMapper(IEmployeeRequestMapper.class);

    @Spy
    private IEmployeeResponseMapper responseResponseMapper = Mappers.getMapper(IEmployeeResponseMapper.class);

    @InjectMocks
    private EmployeeHandler employeeHandler;

    private CreateEmployeeRequestDto createEmployeeRequestDto;
    private RestaurantEmployee savedRestaurantEmployee;

    private static final Long EMPLOYEE_ID = 2L;
    private static final Long RESTAURANT_ID = 3L;

    @BeforeEach
    void setUp() {
        savedRestaurantEmployee = RestaurantEmployee.builder()
                .userId(EMPLOYEE_ID)
                .restaurantId(RESTAURANT_ID)
                .build();

        createEmployeeRequestDto = CreateEmployeeRequestDto.builder()
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .password("PlainPassword123$")
                .build();
    }

    @Test
    @DisplayName("Should create a restaurant employee")
    void shouldCreateRestaurantEmployee() {
        when(employeeServicePort.createEmployee(eq(RESTAURANT_ID), any(UserInformation.class), any(Long.class)))
                .thenReturn(savedRestaurantEmployee);

        var result = employeeHandler.createEmployee(RESTAURANT_ID, createEmployeeRequestDto, 1L);

        var userInformationCaptor = ArgumentCaptor.forClass(UserInformation.class);
        verify(employeeServicePort).createEmployee(eq(RESTAURANT_ID), userInformationCaptor.capture(), any(Long.class));
        var passedUserInformation = userInformationCaptor.getValue();

        assertThat(passedUserInformation.getEmail()).isEqualTo(createEmployeeRequestDto.getEmail());
        assertThat(passedUserInformation.getName()).isEqualTo(createEmployeeRequestDto.getName());

        verify(requestRequestMapper).toUserInformation(createEmployeeRequestDto);
        verify(responseResponseMapper).toResponse(savedRestaurantEmployee);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(savedRestaurantEmployee.getUserId());
        assertThat(result.getRestaurantId()).isEqualTo(savedRestaurantEmployee.getRestaurantId());
    }
}
