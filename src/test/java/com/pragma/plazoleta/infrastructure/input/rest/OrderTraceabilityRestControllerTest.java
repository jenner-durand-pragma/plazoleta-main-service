package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.response.orderstate.OrderTraceabilityResponseDto;
import com.pragma.plazoleta.application.handler.IOrderTraceabilityHandler;
import com.pragma.plazoleta.domain.exception.order.OrderNotFoundException;
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderTraceabilityRestController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfiguration.class,
        CustomAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class OrderTraceabilityRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITokenValidationPort tokenValidationPort;

    @MockBean
    private IOrderTraceabilityHandler orderTraceabilityHandler;

    private UsernamePasswordAuthenticationToken clientAuthentication;

    private static final Long ORDER_ID = 42L;
    private static final Long CLIENT_ID = 10L;

    @BeforeEach
    void setUp() {
        var clientPrincipal = new AuthenticatedUser(
                CLIENT_ID,
                "jenner.durand@plazoleta.com",
                "CLIENT"
        );

        clientAuthentication = new UsernamePasswordAuthenticationToken(
                clientPrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
    }

    @Test
    @DisplayName(
            "Should return 200 OK with traceability response when " +
            "CLIENT requests history of their own order in find by order id for client"
    )
    void shouldReturn200OkWithTraceabilityWhenClientRequestsHistoryOfTheirOrder() throws Exception {
        var responseDto = OrderTraceabilityResponseDto.builder()
                .orderId(ORDER_ID)
                .transitions(List.of())
                .build();

        when(orderTraceabilityHandler.findByOrderIdForClient(ORDER_ID, CLIENT_ID))
                .thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/traceability/orders/42")
                        .with(authentication(clientAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(ORDER_ID));

        verify(orderTraceabilityHandler).findByOrderIdForClient(ORDER_ID, CLIENT_ID);
    }

    @Test
    @DisplayName(
            "Should return 404 Not Found when " +
            "order does not exist in find by order id for client"
    )
    void shouldReturn404NotFoundWhenOrderDoesNotExistInFindByOrderIdForClient() throws Exception {
        doThrow(new OrderNotFoundException(ORDER_ID))
                .when(orderTraceabilityHandler).findByOrderIdForClient(ORDER_ID, CLIENT_ID);

        mockMvc.perform(get("/api/v1/traceability/orders/42")
                        .with(authentication(clientAuthentication)))
                .andExpect(status().isNotFound());

        verify(orderTraceabilityHandler).findByOrderIdForClient(ORDER_ID, CLIENT_ID);
    }

    @Test
    @DisplayName(
            "Should return 403 Forbidden when " +
            "caller does not have CLIENT role in find by order id for client"
    )
    void shouldReturn403ForbiddenWhenCallerDoesNotHaveClientRole() throws Exception {
        var employeePrincipal = new AuthenticatedUser(
                1L,
                "admin@plazoleta.com",
                "EMPLOYEE"
        );
        var employeeAuthentication = new UsernamePasswordAuthenticationToken(
                employeePrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        );

        mockMvc.perform(get("/api/v1/traceability/orders/42")
                        .with(authentication(employeeAuthentication)))
                .andExpect(status().isForbidden());
    }
}