package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.employee.CreateEmployeeRequestDto;
import com.pragma.plazoleta.application.dto.response.employee.EmployeeAssignmentResponseDto;
import com.pragma.plazoleta.application.handler.IEmployeeHandler;
import com.pragma.plazoleta.infrastructure.configuration.SecurityConfiguration;
import com.pragma.plazoleta.infrastructure.configuration.security.CustomAccessDeniedHandler;
import com.pragma.plazoleta.infrastructure.configuration.security.CustomAuthenticationEntryPoint;
import com.pragma.plazoleta.infrastructure.configuration.security.CustomAuthenticationFilter;
import com.pragma.plazoleta.infrastructure.configuration.security.token.ITokenValidationPort;
import com.pragma.plazoleta.infrastructure.configuration.security.token.dto.AuthenticatedUser;
import com.pragma.plazoleta.infrastructure.exceptionhandler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeRestController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfiguration.class,
        CustomAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class EmployeeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITokenValidationPort tokenValidationPort;

    @MockBean
    private IEmployeeHandler employeeHandler;

    private ObjectMapper objectMapper;
    private CreateEmployeeRequestDto validRequest;
    private UsernamePasswordAuthenticationToken userAuthentication;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        validRequest = CreateEmployeeRequestDto.builder()
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .password("PlainPassword123$")
                .build();

        var principal = new AuthenticatedUser(2L, "jenner.durand@plazoleta.com", "OWNER");
        userAuthentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
        );
    }

    @Test
    @DisplayName("Should return 201 with userId and restaurantId when caller is the owner")
    void shouldReturn201() throws Exception {
        var response = EmployeeAssignmentResponseDto.builder()
                .userId(3L)
                .restaurantId(10L)
                .build();

        when(employeeHandler.createEmployee(eq(10L), any(), eq(2L))).thenReturn(response);

        mockMvc.perform(post("/api/v1/restaurants/10/employees")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(3))
                .andExpect(jsonPath("$.restaurantId").value(10));
    }

    @Test
    @DisplayName("Should return 400 when document number is not numeric")
    void shouldReturn400OnInvalidDocument() throws Exception {
        validRequest.setDocumentNumber("invalid.document.number");

        mockMvc.perform(post("/api/v1/restaurants/10/employees")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(employeeHandler, never()).createEmployee(any(), any(), any());
    }

    @Test
    @DisplayName("Should return 400 when email format is invalid")
    void shouldReturn400OnInvalidEmail() throws Exception {
        validRequest.setEmail("invalid.email");

        mockMvc.perform(post("/api/v1/restaurants/10/employees")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }
}
