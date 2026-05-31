package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.restaurant.RestaurantListItemResponseDto;
import com.pragma.plazoleta.application.dto.response.restaurant.RestaurantResponseDto;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IRestaurantResponseMapper {

    RestaurantResponseDto toResponse(Restaurant restaurant);
    RestaurantListItemResponseDto toListItem(Restaurant restaurant);
}
