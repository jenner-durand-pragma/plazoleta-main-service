package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pragma.plazoleta.application.dto.request.order.CreateOrderDishDto;
import com.pragma.plazoleta.application.dto.request.order.CreateOrderRequestDto;
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

@WebMvcTest(controllers = RestaurantOrderRestController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfiguration.class,
        CustomAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class RestaurantOrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITokenValidationPort tokenValidationPort;

    @MockBean
    private IOrderHandler orderHandler;

    private ObjectMapper objectMapper;
    private CreateOrderRequestDto validRequest;
    private UsernamePasswordAuthenticationToken clientAuthentication;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        validRequest = CreateOrderRequestDto.builder()
                .items(List.of(
                        CreateOrderDishDto.builder().dishId(1L).quantity(2).build()
                ))
                .build();

        var clientPrincipal = new AuthenticatedUser(5L, "client@plazoleta.com", "CLIENT");
        clientAuthentication = new UsernamePasswordAuthenticationToken(
                clientPrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
    }

    @Test
    @DisplayName("Should return 201 Created when CLIENT places a valid order in create order")
    void shouldReturn201CreatedWhenClientPlacesAValidOrderInCreateOrder() throws Exception {
        var response = OrderResponseDto.builder()
                .id(42L)
                .restaurantId(10L)
                .clientId(5L)
                .status(OrderStatus.PENDING)
                .build();

        when(orderHandler.createOrder(eq(10L), any(), eq(5L))).thenReturn(response);

        mockMvc.perform(post("/api/v1/restaurants/10/orders")
                        .with(authentication(clientAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when items list is empty in create order")
    void shouldReturn400BadRequestWhenItemsListIsEmptyInCreateOrder() throws Exception {
        var emptyRequest = CreateOrderRequestDto.builder().items(List.of()).build();

        mockMvc.perform(post("/api/v1/restaurants/10/orders")
                        .with(authentication(clientAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).createOrder(any(), any(), any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when an item has quantity zero in create order")
    void shouldReturn400BadRequestWhenAnItemHasQuantityZeroInCreateOrder() throws Exception {
        var badRequest = CreateOrderRequestDto.builder()
                .items(List.of(CreateOrderDishDto.builder().dishId(1L).quantity(0).build()))
                .build();

        mockMvc.perform(post("/api/v1/restaurants/10/orders")
                        .with(authentication(clientAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).createOrder(any(), any(), any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when dish id is missing in create order")
    void shouldReturn400BadRequestWhenDishIdIsMissingInCreateOrder() throws Exception {
        var badRequest = CreateOrderRequestDto.builder()
                .items(List.of(CreateOrderDishDto.builder().quantity(2).build()))
                .build();

        mockMvc.perform(post("/api/v1/restaurants/10/orders")
                        .with(authentication(clientAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).createOrder(any(), any(), any());
    }
}
