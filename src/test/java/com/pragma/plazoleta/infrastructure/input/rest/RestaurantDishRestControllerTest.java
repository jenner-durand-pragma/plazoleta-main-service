package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.dish.CreateDishRequestDto;
import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.dish.DishListItemResponseDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestaurantDishRestController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfiguration.class,
        CustomAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class RestaurantDishRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITokenValidationPort tokenValidationPort;

    @MockBean
    private IDishHandler dishHandler;

    private ObjectMapper objectMapper;
    private CreateDishRequestDto validRequest;

    private UsernamePasswordAuthenticationToken ownerAuthentication;
    private UsernamePasswordAuthenticationToken clientAuthentication;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        validRequest = CreateDishRequestDto.builder()
                .name("Pineapple Pizza")
                .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                .price(15000)
                .categoryId(1L)
                .imageUrl("https://dishes.example.com/dish.png")
                .build();

        var ownerPrincipal = new AuthenticatedUser(2L, "jenner.durand@plazoleta.com", "OWNER");
        ownerAuthentication = new UsernamePasswordAuthenticationToken(
                ownerPrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
        );

         var clientPrincipal = new AuthenticatedUser(5L, "client@plazoleta.com", "CLIENT");
         clientAuthentication = new UsernamePasswordAuthenticationToken(
                 clientPrincipal,
                 null,
                 List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
         );
    }

    @Test
    @DisplayName("Should return 201 Created when dish data is valid in create dish")
    void shouldReturn201CreatedWhenDishDataIsValidInCreateDish() throws Exception {
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

        when(dishHandler.createDish(eq(10L), any(), eq(2L))).thenReturn(response);

        mockMvc.perform(post("/api/v1/restaurants/10/dishes")
                        .with(authentication(ownerAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when price is zero in create dish")
    void shouldReturn400BadRequestWhenPriceIsZeroInCreateDish() throws Exception {
        validRequest.setPrice(0);

        mockMvc.perform(post("/api/v1/restaurants/10/dishes")
                        .with(authentication(ownerAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(dishHandler, never()).createDish(any(), any(), any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when price is negative in create dish")
    void shouldReturn400BadRequestWhenPriceIsNegativeInCreateDish() throws Exception {
        validRequest.setPrice(-100);

        mockMvc.perform(post("/api/v1/restaurants/10/dishes")
                        .with(authentication(ownerAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(dishHandler, never()).createDish(any(), any(), any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when name is blank in create dish")
    void shouldReturn400BadRequestWhenNameIsBlankInCreateDish() throws Exception {
        validRequest.setName("");

        mockMvc.perform(post("/api/v1/restaurants/10/dishes")
                        .with(authentication(ownerAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(dishHandler, never()).createDish(any(), any(), any());
    }

    @Test
    @DisplayName("Should return 200 OK with paged dishes when CLIENT requests the list in list dishes")
    void shouldReturn200OkWithPagedDishesWhenClientRequestsTheListInListDishes() throws Exception {
        var items = List.of(
                DishListItemResponseDto.builder()
                        .id(1L)
                        .name("Pineapple Pizza")
                        .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                        .price(15000)
                        .imageUrl("https://dishes.example.com/dish.png")
                        .categoryName("Main Course")
                        .build()
        );
        var paged = new PagedResponseDto<>(items, 0, 10, 1L, 1);

        when(dishHandler.listDishesByRestaurant(10L, 1L, 0, 10)).thenReturn(paged);

        mockMvc.perform(get("/api/v1/restaurants/10/dishes")
                        .with(authentication(clientAuthentication))
                        .param("categoryId", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Pineapple Pizza"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Should use default pagination and null category when query params are omitted in list dishes")
    void shouldUseDefaultPaginationAndNullCategoryWhenQueryParamsAreOmittedInListDishes() throws Exception {
        var items = List.of(
                DishListItemResponseDto.builder()
                        .id(1L)
                        .name("Pineapple Pizza")
                        .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                        .price(15000)
                        .imageUrl("https://dishes.example.com/dish.png")
                        .categoryName("Main Course")
                        .build()
        );
        var paged = new PagedResponseDto<>(items, 0, 10, 1L, 1);

        when(dishHandler.listDishesByRestaurant(10L, null, 0, 10)).thenReturn(paged);

        mockMvc.perform(get("/api/v1/restaurants/10/dishes")
                        .with(authentication(clientAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Pineapple Pizza"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(dishHandler).listDishesByRestaurant(10L, null, 0, 10);
    }
}
