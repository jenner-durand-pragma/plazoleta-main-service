package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.restaurant.CreateRestaurantRequestDto;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IRestaurantRequestMapper {

    @Mapping(target = "id", ignore = true)
    Restaurant toRestaurant(CreateRestaurantRequestDto dto);

}
