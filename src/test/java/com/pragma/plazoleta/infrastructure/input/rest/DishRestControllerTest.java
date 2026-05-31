package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.dish.UpdateDishRequestDto;
import com.pragma.plazoleta.application.dto.request.dish.UpdateDishStatusRequestDto;
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
    private UsernamePasswordAuthenticationToken ownerAuthentication;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        var ownerPrincipal = new AuthenticatedUser(2L, "jenner.durand@plazoleta.com", "OWNER");
        ownerAuthentication = new UsernamePasswordAuthenticationToken(
                ownerPrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
        );
    }

    @Test
    @DisplayName("Should return 200 OK when dish update data is valid in update dish")
    void shouldReturn200OkWhenDishUpdateDataIsValidInUpdateDish() throws Exception {
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
                        .with(authentication(ownerAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(20000))
                .andExpect(jsonPath("$.description").value("Change Description"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when update price is zero or negative in update dish")
    void shouldReturn400BadRequestWhenUpdatePriceIsZeroOrNegativeInUpdateDish() throws Exception {
        var badUpdateRequest = UpdateDishRequestDto.builder()
                .price(0)
                .description("Change Description")
                .build();

        mockMvc.perform(patch("/api/v1/dishes/1")
                        .with(authentication(ownerAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUpdateRequest)))
                .andExpect(status().isBadRequest());

        verify(dishHandler, never()).updateDish(any(), any(), eq(2L));
    }

    @Test
    @DisplayName("Should return 200 OK when dish status is updated successfully in update dish status")
    void shouldReturn200OkWhenDishStatusIsUpdatedSuccessfullyInUpdateDishStatus() throws Exception {
        var request = UpdateDishStatusRequestDto.builder()
                .active(false)
                .build();
        var response = DishResponseDto.builder()
                .id(1L)
                .name("Pineapple Pizza")
                .description("Change Description")
                .price(20000)
                .categoryName("Main Course")
                .restaurantId(10L)
                .imageUrl("https://dishes.example.com/dish.png")
                .active(false)
                .build();

        when(dishHandler.updateDishStatus(eq(1L), any(UpdateDishStatusRequestDto.class), eq(2L)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/dishes/1/status")
                        .with(authentication(ownerAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when active field is missing in update dish status")
    void shouldReturn400BadRequestWhenActiveFieldIsMissingInUpdateDishStatus() throws Exception {
        var badRequest = UpdateDishStatusRequestDto.builder()
                .active(null)
                .build();

        mockMvc.perform(patch("/api/v1/dishes/1/status")
                        .with(authentication(ownerAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(dishHandler, never()).updateDishStatus(any(), any(), any());
    }
}