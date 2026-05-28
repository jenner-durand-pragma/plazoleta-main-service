package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.restaurant.CreateRestaurantRequestDto;
import com.pragma.plazoleta.application.dto.response.restaurant.RestaurantResponseDto;
import com.pragma.plazoleta.application.handler.IRestaurantHandler;
import com.pragma.plazoleta.infrastructure.exceptionhandler.GlobalExceptionHandler;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestaurantRestController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        })
@Import(GlobalExceptionHandler.class)
@DisplayName("RestaurantRestController - Create Restaurant (HU02)")
class RestaurantRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRestaurantHandler restaurantHandler;

    private ObjectMapper objectMapper;
    private CreateRestaurantRequestDto validRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        validRequest = CreateRestaurantRequestDto.builder()
                .name("Pizza Place")
                .address("Example Street 123")
                .ownerId(5L)
                .phone("+573005698325")
                .logoUrl("https://logo.example.com/image.png")
                .nit("9001234567")
                .build();
    }

    @Test
    @DisplayName("Should return 201 when restaurant data is valid")
    void shouldReturn201() throws Exception {
        var response = RestaurantResponseDto.builder()
                .id(1L)
                .name("Pizza Place")
                .address("Example Street 123")
                .ownerId(5L)
                .phone("+573005698325")
                .logoUrl("https://logo.example.com/image.png")
                .nit("9001234567")
                .build();
        when(restaurantHandler.createRestaurant(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nit").value("9001234567"));
    }

    @Test
    @DisplayName("Should return 400 when NIT is not numeric")
    void shouldReturn400WhenNitNotNumeric() throws Exception {
        validRequest.setNit("ABC123");

        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(restaurantHandler, never()).createRestaurant(any());
    }

    @Test
    @DisplayName("Should return 400 when name contains only numbers")
    void shouldReturn400WhenNameOnlyNumbers() throws Exception {
        validRequest.setName("12345");

        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(restaurantHandler, never()).createRestaurant(any());
    }

    @Test
    @DisplayName("Should return 503 when a required external service fails (FeignException)")
    void shouldReturn503WhenExternalServiceFails() throws Exception {
        var feignExceptionMock = mock(FeignException.class);
        when(restaurantHandler.createRestaurant(any())).thenThrow(feignExceptionMock);

        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status")
                        .value(503))
                .andExpect(jsonPath("$.message")
                        .value("A required service is temporarily unavailable"));
    }

    @Test
    @DisplayName("Should return 400 when phone format is invalid (contains letters)")
    void shouldReturn400WhenPhoneInvalid() throws Exception {
        validRequest.setPhone("+57300A5698");

        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(restaurantHandler, never()).createRestaurant(any());
    }

    @Test
    @DisplayName("Should return 400 when ownerId is null")
    void shouldReturn400WhenOwnerIdIsNull() throws Exception {
        validRequest.setOwnerId(null);

        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(restaurantHandler, never()).createRestaurant(any());
    }
}
