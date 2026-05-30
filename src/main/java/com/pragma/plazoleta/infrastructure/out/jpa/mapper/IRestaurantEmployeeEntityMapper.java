package com.pragma.plazoleta.infrastructure.out.jpa.mapper;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.infrastructure.out.jpa.entity.RestaurantEmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IRestaurantEmployeeEntityMapper {

    RestaurantEmployeeEntity toEntity(RestaurantEmployee model);
    RestaurantEmployee toModel(RestaurantEmployeeEntity entity);
}
