package com.pragma.plazoleta.infrastructure.out.jpa.mapper;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.infrastructure.out.jpa.entity.OrderDishEntity;
import com.pragma.plazoleta.infrastructure.out.jpa.entity.OrderDishId;
import com.pragma.plazoleta.infrastructure.out.jpa.entity.OrderEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = { IDishEntityMapper.class, IRestaurantEntityMapper.class },
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface IOrderEntityMapper {

    @Mapping(source = "items", target = "orderDishes")
    OrderEntity toEntity(Order order);

    @Mapping(source = "orderDishes", target = "items")
    Order toModel(OrderEntity entity);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "id", source = ".")
    OrderDishEntity toOrderDishEntity(OrderDish orderDish);

    OrderDishId toOrderDishId(OrderDish orderDish);

    @Mapping(target = "dishId", source = "dish.id")
    @Mapping(target = "dish.id", source = "dish.id")
    @Mapping(target = "orderId", source = "order.id")
    OrderDish toOrderDish(OrderDishEntity entity);

    @AfterMapping
    default void linkOrderDishes(@MappingTarget OrderEntity entity) {
        if (entity.getOrderDishes() != null) {
            entity.getOrderDishes().forEach(orderDish -> orderDish.setOrder(entity));
        }
    }
}
