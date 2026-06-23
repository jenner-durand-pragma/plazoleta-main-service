package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.orderreport.EmployeeRankingResponseDto;
import com.pragma.plazoleta.application.dto.response.orderreport.OrderEfficiencyResponseDto;
import com.pragma.plazoleta.application.dto.response.orderstate.OrderUserResponseDto;
import com.pragma.plazoleta.application.handler.IOrderReportHandler;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderReportRestController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfiguration.class,
        CustomAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class OrderReportRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITokenValidationPort tokenValidationPort;

    @MockBean
    private IOrderReportHandler orderReportHandler;

    private UsernamePasswordAuthenticationToken ownerAuthentication;

    private static final Long RESTAURANT_ID = 10L;
    private static final Long OWNER_ID = 2L;
    private static final Integer PAGE = 0;
    private static final Integer SIZE = 10;

    @BeforeEach
    void setUp() {
        var ownerPrincipal = new AuthenticatedUser(OWNER_ID, "owner@plazoleta.com", "OWNER");

        ownerAuthentication = new UsernamePasswordAuthenticationToken(
                ownerPrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
        );
    }

    @Test
    @DisplayName(
            "Should return 200 OK with order efficiency paged response when " +
            "parameters are valid in get order efficiency"
    )
    void shouldReturn200OkWithOrderEfficiencyPagedResponseWhenParametersAreValidInGetOrderEfficiency()
            throws Exception {
        var clientResponse = OrderUserResponseDto.builder()
                .id(5L)
                .name("Jenner")
                .lastName("Durand")
                .build();

        var orderEfficiencyDto = OrderEfficiencyResponseDto.builder()
                .orderId(42L)
                .durationMinutes(15.5)
                .client(clientResponse)
                .build();

        var pagedResponse = new PagedResponseDto<OrderEfficiencyResponseDto>();
        pagedResponse.setItems(List.of(orderEfficiencyDto));

        when(orderReportHandler.getOrderEfficiency(RESTAURANT_ID, OWNER_ID, PAGE, SIZE))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/efficiency", RESTAURANT_ID)
                        .param("page", PAGE.toString())
                        .param("size", SIZE.toString())
                        .with(authentication(ownerAuthentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].orderId").value(42))
                .andExpect(jsonPath("$.items[0].durationMinutes").value(15.5))
                .andExpect(jsonPath("$.items[0].client.name").value("Jenner"));

        verify(orderReportHandler).getOrderEfficiency(RESTAURANT_ID, OWNER_ID, PAGE, SIZE);
    }

    @Test
    @DisplayName(
            "Should return 200 OK with employee ranking paged response when " +
            "parameters are valid in get employee ranking"
    )
    void shouldReturn200OkWithEmployeeRankingPagedResponseWhenParametersAreValidInGetEmployeeRanking()
            throws Exception {
        var employeeResponse = OrderUserResponseDto.builder()
                .id(6L)
                .name("Employee")
                .lastName("Second")
                .build();

        var employeeRankingDto = EmployeeRankingResponseDto.builder()
                .employee(employeeResponse)
                .averageMinutes(12.5)
                .ordersHandled(20L)
                .build();

        var pagedResponse = new PagedResponseDto<EmployeeRankingResponseDto>();
        pagedResponse.setItems(List.of(employeeRankingDto));

        when(orderReportHandler.getEmployeeRanking(RESTAURANT_ID, OWNER_ID, PAGE, SIZE))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/employees-ranking", RESTAURANT_ID)
                        .param("page", PAGE.toString())
                        .param("size", SIZE.toString())
                        .with(authentication(ownerAuthentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].employee.id").value(6))
                .andExpect(jsonPath("$.items[0].averageMinutes").value(12.5))
                .andExpect(jsonPath("$.items[0].ordersHandled").value(20));

        verify(orderReportHandler).getEmployeeRanking(RESTAURANT_ID, OWNER_ID, PAGE, SIZE);
    }
}
