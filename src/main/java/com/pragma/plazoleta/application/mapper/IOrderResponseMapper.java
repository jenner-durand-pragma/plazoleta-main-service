package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.order.OrderDishResponseDto;
import com.pragma.plazoleta.application.dto.response.order.OrderResponseDto;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IOrderResponseMapper {

    @Mapping(target = "restaurantId", source = "restaurant.id")
    OrderResponseDto toResponse(Order order);

    @Mapping(target = "dishName", source = "dish.name")
    OrderDishResponseDto toDishDto(OrderDish item);
}
