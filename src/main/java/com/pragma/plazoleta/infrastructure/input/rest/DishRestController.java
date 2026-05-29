package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.dish.CreateDishRequestDto;
import com.pragma.plazoleta.application.dto.response.dish.DishResponseDto;
import com.pragma.plazoleta.application.handler.IDishHandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
@Tag(name = "Dishes", description = "Dish management operations")
public class DishRestController {

    private final IDishHandler dishHandler;

    @PostMapping
    public ResponseEntity<DishResponseDto> createDish(
            @Valid @RequestBody CreateDishRequestDto request
    ) {
        return null;
    }
}
