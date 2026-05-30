package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEmployeeEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRestaurantEmployeeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantEmployeeJpaAdapter implements IRestaurantEmployeePersistencePort {

    private final IRestaurantEmployeeRepository repository;
    private final IRestaurantEmployeeEntityMapper mapper;

    @Override
    public RestaurantEmployee save(RestaurantEmployee employee) {
        return null;
    }
}
