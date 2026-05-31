package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.restaurant.CreateRestaurantRequestDto;
import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.restaurant.RestaurantListItemResponseDto;
import com.pragma.plazoleta.application.dto.response.restaurant.RestaurantResponseDto;

public interface IRestaurantHandler {

    RestaurantResponseDto createRestaurant(CreateRestaurantRequestDto request);
    PagedResponseDto<RestaurantListItemResponseDto> listRestaurants(Integer page, Integer size);
}
