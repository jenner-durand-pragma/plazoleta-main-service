package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.order.CreateOrderRequestDto;
import com.pragma.plazoleta.application.dto.response.order.OrderResponseDto;
import com.pragma.plazoleta.application.handler.IOrderHandler;
import com.pragma.plazoleta.infrastructure.configuration.security.annotation.IsClient;
import com.pragma.plazoleta.infrastructure.configuration.security.token.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Operations for placing orders within a specific restaurant context")
public class RestaurantOrderRestController {

    private final IOrderHandler orderHandler;

    @IsClient
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateOrderRequestDto request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return null;
    }
}
