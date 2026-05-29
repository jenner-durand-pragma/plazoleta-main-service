package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.dish.CreateDishRequestDto;
import com.pragma.plazoleta.application.dto.request.dish.UpdateDishRequestDto;
import com.pragma.plazoleta.application.dto.response.dish.DishResponseDto;

public interface IDishHandler {

    DishResponseDto createDish(CreateDishRequestDto request);
    DishResponseDto updateDish(Long dishId, UpdateDishRequestDto request);

}
