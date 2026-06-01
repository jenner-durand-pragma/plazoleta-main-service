package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.order.CreateOrderDishDto;
import com.pragma.plazoleta.application.dto.request.order.CreateOrderRequestDto;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IOrderRequestMapper {

    @Mapping(target = "restaurant.id", source = "restaurantId")
    Order toOrder(CreateOrderRequestDto dto, Long restaurantId);

    @Mapping(target = "dish.id", source = "dishId")
    OrderDish toOrderDish(CreateOrderDishDto dto);
}
