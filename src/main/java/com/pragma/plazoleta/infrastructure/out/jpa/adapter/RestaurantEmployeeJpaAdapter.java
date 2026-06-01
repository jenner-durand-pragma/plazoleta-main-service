package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEmployeeEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRestaurantEmployeeRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class RestaurantEmployeeJpaAdapter implements IRestaurantEmployeePersistencePort {

    private final IRestaurantEmployeeRepository repository;
    private final IRestaurantEmployeeEntityMapper mapper;

    @Override
    public RestaurantEmployee save(RestaurantEmployee employee) {
        var entity = mapper.toEntity(employee);
        var saved = repository.save(entity);

        return mapper.toModel(saved);
    }

    @Override
    public Optional<Long> findRestaurantIdByUserId(Long userId) {
        return Optional.empty();
    }
}
