package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.dish.CreateDishRequestDto;
import com.pragma.plazoleta.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IDishRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "category.id", source = "dto.categoryId")
    @Mapping(target = "restaurant.id", source = "restaurantId")
    Dish toDish(CreateDishRequestDto dto, Long restaurantId);
}
