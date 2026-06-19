package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.order.OrderResponseDto;
import com.pragma.plazoleta.application.handler.IOrderHandler;
import com.pragma.plazoleta.domain.enums.OrderStatus;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderRestController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfiguration.class,
        CustomAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITokenValidationPort tokenValidationPort;

    @MockBean
    private IOrderHandler orderHandler;

    private UsernamePasswordAuthenticationToken employeeAuthentication;

    @BeforeEach
    void setUp() {
        var employeePrincipal = new AuthenticatedUser(7L, "employee@plazoleta.com", "EMPLOYEE");
        employeeAuthentication = new UsernamePasswordAuthenticationToken(
                employeePrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        );
    }

    @Test
    @DisplayName(
            "Should return 200 OK with paged orders when " +
            "EMPLOYEE requests with valid status in list orders by status"
    )
    void shouldReturn200OkWithPagedOrdersWhenEmployeeRequestsWithValidStatusInListOrdersByStatus() throws Exception {
        var items = List.of(
                OrderResponseDto.builder()
                        .id(42L)
                        .status(OrderStatus.PENDING)
                        .restaurantId(10L)
                        .clientId(5L)
                        .build()
        );
        var paged = new PagedResponseDto<>(items, 0, 10, 1L, 1);

        when(orderHandler.listOrdersByStatus(OrderStatus.PENDING, 7L, 0, 10))
                .thenReturn(paged);

        mockMvc.perform(get("/api/v1/orders")
                        .with(authentication(employeeAuthentication))
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value(42))
                .andExpect(jsonPath("$.items[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when status query param is missing in list orders by status")
    void shouldReturn400BadRequestWhenStatusQueryParamIsMissingInListOrdersByStatus() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                        .with(authentication(employeeAuthentication)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).listOrdersByStatus(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when status value is invalid in list orders by status")
    void shouldReturn400BadRequestWhenStatusValueIsInvalidInListOrdersByStatus() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                        .with(authentication(employeeAuthentication))
                        .param("status", "NOT_A_REAL_STATUS"))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).listOrdersByStatus(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should use default pagination when query params are omitted in list orders by status")
    void shouldUseDefaultPaginationWhenQueryParamsAreOmittedInListOrdersByStatus() throws Exception {
        var paged = new PagedResponseDto<OrderResponseDto>(
                List.of(),
                0,
                10,
                0L,
                0
        );

        when(orderHandler.listOrdersByStatus(OrderStatus.READY, 7L, 0, 10))
                .thenReturn(paged);

        mockMvc.perform(get("/api/v1/orders")
                        .with(authentication(employeeAuthentication))
                        .param("status", "READY"))
                .andExpect(status().isOk());

        verify(orderHandler).listOrdersByStatus(OrderStatus.READY, 7L, 0, 10);
    }

    @Test
    @DisplayName(
            "Should return 200 OK with order in IN_PREPARATION when " +
            "EMPLOYEE assigns a pending order in assign order"
    )
    void shouldReturn200OkWithOrderInInPreparationWhenEmployeeAssignsAPendingOrderInAssignOrder() throws Exception {
        var response = OrderResponseDto.builder()
                .id(42L)
                .status(OrderStatus.IN_PREPARATION)
                .chefId(7L)
                .restaurantId(10L)
                .clientId(5L)
                .build();

        when(orderHandler.assignOrder(42L, 7L)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/orders/42/assign")
                        .with(authentication(employeeAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.status").value("IN_PREPARATION"))
                .andExpect(jsonPath("$.chefId").value(7));

        verify(orderHandler).assignOrder(42L, 7L);
    }

    @Test
    @DisplayName(
            "Should return 200 with READY when " +
            "EMPLOYEE marks an in-preparation order ready in mark order ready"
    )
    void shouldReturn200WithReadyAndPinWhenEmployeeMarksAnInPreparationOrderReadyInMarkOrderReady() throws Exception {
        var response = OrderResponseDto.builder()
                .id(42L)
                .status(OrderStatus.READY)
                .chefId(7L)
                .restaurantId(10L)
                .clientId(5L)
                .build();

        when(orderHandler.markOrderReady(42L, 7L)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/orders/42/ready")
                        .with(authentication(employeeAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));
    }
}
