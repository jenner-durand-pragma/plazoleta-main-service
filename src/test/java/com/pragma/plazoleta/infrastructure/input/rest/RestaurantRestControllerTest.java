package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.restaurant.CreateRestaurantRequestDto;
import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.restaurant.RestaurantListItemResponseDto;
import com.pragma.plazoleta.application.dto.response.restaurant.RestaurantResponseDto;
import com.pragma.plazoleta.application.handler.IRestaurantHandler;
import com.pragma.plazoleta.infrastructure.configuration.SecurityConfiguration;
import com.pragma.plazoleta.infrastructure.configuration.security.CustomAccessDeniedHandler;
import com.pragma.plazoleta.infrastructure.configuration.security.CustomAuthenticationEntryPoint;
import com.pragma.plazoleta.infrastructure.configuration.security.CustomAuthenticationFilter;
import com.pragma.plazoleta.infrastructure.configuration.security.token.ITokenValidationPort;
import com.pragma.plazoleta.infrastructure.configuration.security.token.dto.AuthenticatedUser;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestaurantRestController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfiguration.class,
        CustomAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class RestaurantRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITokenValidationPort tokenValidationPort;

    @MockBean
    private IRestaurantHandler restaurantHandler;

    private ObjectMapper objectMapper;
    private CreateRestaurantRequestDto validRequest;
    private UsernamePasswordAuthenticationToken adminAuthentication;
    private UsernamePasswordAuthenticationToken clientAuthentication;

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

        var adminPrincipal = new AuthenticatedUser(2L, "jenner.durand@plazoleta.com", "ADMIN");
        adminAuthentication = new UsernamePasswordAuthenticationToken(
                adminPrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        var clientPrincipal = new AuthenticatedUser(5L, "client@plazoleta.com", "CLIENT");
        clientAuthentication = new UsernamePasswordAuthenticationToken(
                clientPrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
    }

    @Test
    @DisplayName("Should return 201 Created when restaurant data is valid in create restaurant")
    void shouldReturn201CreatedWhenRestaurantDataIsValidInCreateRestaurant() throws Exception {
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
                        .with(authentication(adminAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nit").value("9001234567"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when NIT is not numeric in create restaurant")
    void shouldReturn400BadRequestWhenNitIsNotNumericInCreateRestaurant() throws Exception {
        validRequest.setNit("ABC123");

        mockMvc.perform(post("/api/v1/restaurants")
                        .with(authentication(adminAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(restaurantHandler, never()).createRestaurant(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when name contains only numbers in create restaurant")
    void shouldReturn400BadRequestWhenNameContainsOnlyNumbersInCreateRestaurant() throws Exception {
        validRequest.setName("12345");

        mockMvc.perform(post("/api/v1/restaurants")
                        .with(authentication(adminAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(restaurantHandler, never()).createRestaurant(any());
    }

    @Test
    @DisplayName("Should return 503 Service Unavailable when a required external service fails in create restaurant")
    void shouldReturn503ServiceUnavailableWhenExternalServiceFailsInCreateRestaurant() throws Exception {
        var feignExceptionMock = mock(FeignException.class);
        when(restaurantHandler.createRestaurant(any())).thenThrow(feignExceptionMock);

        mockMvc.perform(post("/api/v1/restaurants")
                        .with(authentication(adminAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status")
                        .value(503))
                .andExpect(jsonPath("$.message")
                        .value("A required service is temporarily unavailable"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when phone format is invalid in create restaurant")
    void shouldReturn400BadRequestWhenPhoneFormatIsInvalidInCreateRestaurant() throws Exception {
        validRequest.setPhone("+57300A5698");

        mockMvc.perform(post("/api/v1/restaurants")
                        .with(authentication(adminAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(restaurantHandler, never()).createRestaurant(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when ownerId is null in create restaurant")
    void shouldReturn400BadRequestWhenOwnerIdIsNullInCreateRestaurant() throws Exception {
        validRequest.setOwnerId(null);

        mockMvc.perform(post("/api/v1/restaurants")
                        .with(authentication(adminAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(restaurantHandler, never()).createRestaurant(any());
    }

    @Test
    @DisplayName("Should return 200 OK with paged restaurants when CLIENT requests the list in list restaurants")
    void shouldReturn200OkWithPagedRestaurantsWhenClientRequestsTheListInListRestaurants() throws Exception {
        var items = List.of(
                new RestaurantListItemResponseDto(1L, "Andina", "http://a.png"),
                new RestaurantListItemResponseDto(2L, "Burger", "http://b.png")
        );
        var paged = new PagedResponseDto<>(items, 0, 10, 2L, 1);
        when(restaurantHandler.listRestaurants(0, 10)).thenReturn(paged);

        mockMvc.perform(get("/api/v1/restaurants")
                        .with(authentication(clientAuthentication))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].name").value("Andina"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Should use default page zero and size ten when query params are omitted in list restaurants")
    void shouldUseDefaultPageZeroAndSizeTenWhenQueryParamsAreOmittedInListRestaurants() throws Exception {
        when(restaurantHandler.listRestaurants(0, 10))
                .thenReturn(new PagedResponseDto<>(List.of(), 0, 10, 0L, 0));

        mockMvc.perform(get("/api/v1/restaurants")
                        .with(authentication(clientAuthentication)))
                .andExpect(status().isOk());

        verify(restaurantHandler).listRestaurants(0, 10);
    }
}
