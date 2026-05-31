package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    @Override
    public Dish save(Dish dish) {
        var entity = dishEntityMapper.toEntity(dish);
        var saved = dishRepository.save(entity);

        return dishEntityMapper.toModel(saved);
    }

    @Override
    public Dish findById(Long id) {
        return dishRepository.findById(id)
                .map(dishEntityMapper::toModel)
                .orElse(null);
    }

    @Override
    public PagedResult<Dish> findActiveByRestaurantAndCategoryPaginated(
            Long restaurantId,
            Long categoryId,
            Integer page,
            Integer size
    ) {
        return null;
    }
}
