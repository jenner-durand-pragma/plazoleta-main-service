package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.dish.CreateDishRequestDto;
import com.pragma.plazoleta.application.dto.request.dish.UpdateDishRequestDto;
import com.pragma.plazoleta.application.dto.request.dish.UpdateDishStatusRequestDto;
import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.dish.DishListItemResponseDto;
import com.pragma.plazoleta.application.dto.response.dish.DishResponseDto;

public interface IDishHandler {

    DishResponseDto createDish(CreateDishRequestDto request, Long ownerId);
    DishResponseDto updateDish(Long dishId, UpdateDishRequestDto request, Long ownerId);
    DishResponseDto updateDishStatus(Long dishId, UpdateDishStatusRequestDto request, Long ownerId);

    PagedResponseDto<DishListItemResponseDto> listDishesByRestaurant(
            Long restaurantId,
            Long categoryId,
            Integer page,
            Integer size
    );
}
