package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.dish.CreateDishRequestDto;
import com.pragma.plazoleta.application.dto.request.dish.UpdateDishRequestDto;
import com.pragma.plazoleta.application.dto.response.dish.DishResponseDto;
import com.pragma.plazoleta.application.handler.IDishHandler;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DishRestController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfiguration.class,
        CustomAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class DishRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITokenValidationPort tokenValidationPort;

    @MockBean
    private IDishHandler dishHandler;

    private ObjectMapper objectMapper;
    private CreateDishRequestDto validRequest;
    private UsernamePasswordAuthenticationToken userAuthentication;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        validRequest = CreateDishRequestDto.builder()
                .name("Pineapple Pizza")
                .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                .price(15000)
                .categoryId(1L)
                .restaurantId(10L)
                .imageUrl("https://dishes.example.com/dish.png")
                .build();

        var principal = new AuthenticatedUser(2L, "jenner.durand@plazoleta.com", "OWNER");
        userAuthentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
        );
    }

    @Test
    @DisplayName("Should return 201 when dish data is valid")
    void shouldReturn201() throws Exception {
        var response = DishResponseDto.builder()
                .id(1L)
                .name("Pineapple Pizza")
                .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                .price(15000)
                .categoryName("Main Course")
                .restaurantId(10L)
                .imageUrl("https://dishes.example.com/dish.png")
                .active(true)
                .build();

        when(dishHandler.createDish(any(), eq(2L))).thenReturn(response);

        mockMvc.perform(post("/api/v1/dishes")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should return 400 when price is zero")
    void shouldReturn400WhenPriceIsZero() throws Exception {
        validRequest.setPrice(0);

        mockMvc.perform(post("/api/v1/dishes")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(dishHandler, never()).createDish(any(), eq(2L));
    }

    @Test
    @DisplayName("Should return 400 when price is negative")
    void shouldReturn400WhenPriceIsNegative() throws Exception {
        validRequest.setPrice(-100);

        mockMvc.perform(post("/api/v1/dishes")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when name is blank")
    void shouldReturn400WhenNameBlank() throws Exception {
        validRequest.setName("");

        mockMvc.perform(post("/api/v1/dishes")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 200 when dish update is valid")
    void shouldReturn200OnUpdate() throws Exception {
        var updateRequest = UpdateDishRequestDto.builder()
                .price(20000)
                .description("Change Description")
                .build();
        var response = DishResponseDto.builder()
                .id(1L)
                .name("Pineapple Pizza")
                .description("Change Description")
                .price(20000)
                .categoryName("Main Course")
                .restaurantId(10L)
                .imageUrl("https://dishes.example.com/dish.png")
                .active(true)
                .build();

        when(dishHandler.updateDish(eq(1L), any(UpdateDishRequestDto.class), eq(2L))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/dishes/1")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(20000))
                .andExpect(jsonPath("$.description").value("Change Description"));
    }

    @Test
    @DisplayName("Should return 400 when update price is zero or negative")
    void shouldReturn400OnInvalidUpdatePrice() throws Exception {
        var badUpdateRequest = UpdateDishRequestDto.builder()
                .price(0)
                .description("Change Description")
                .build();

        mockMvc.perform(patch("/api/v1/dishes/1")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUpdateRequest)))
                .andExpect(status().isBadRequest());

        verify(dishHandler, never()).updateDish(any(), any(), eq(2L));
    }
}
