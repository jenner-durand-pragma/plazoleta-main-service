package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.dish.DishResponseDto;
import com.pragma.plazoleta.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IDishResponseMapper {

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "restaurant.id", target = "restaurantId")
    DishResponseDto toResponse(Dish dish);
}
