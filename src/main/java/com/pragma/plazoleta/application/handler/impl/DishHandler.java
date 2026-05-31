package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.dish.CreateDishRequestDto;
import com.pragma.plazoleta.application.dto.request.dish.UpdateDishRequestDto;
import com.pragma.plazoleta.application.dto.request.dish.UpdateDishStatusRequestDto;
import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.dish.DishListItemResponseDto;
import com.pragma.plazoleta.application.dto.response.dish.DishResponseDto;
import com.pragma.plazoleta.application.handler.IDishHandler;
import com.pragma.plazoleta.application.mapper.IDishRequestMapper;
import com.pragma.plazoleta.application.mapper.IDishResponseMapper;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DishHandler implements IDishHandler {

    private final IDishServicePort dishServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;

    @Override
    @Transactional
    public DishResponseDto createDish(Long restaurantId, CreateDishRequestDto request, Long ownerId) {
        var dishToCreate = dishRequestMapper.toDish(request);
        var dishCreated = dishServicePort.createDish(restaurantId, dishToCreate, ownerId);

        return dishResponseMapper.toResponse(dishCreated);
    }

    @Override
    @Transactional
    public DishResponseDto updateDish(Long dishId, UpdateDishRequestDto request, Long ownerId) {
        var updated = dishServicePort.updateDish(
                dishId,
                request.getPrice(),
                request.getDescription(),
                ownerId
        );

        return dishResponseMapper.toResponse(updated);
    }

    @Override
    public DishResponseDto updateDishStatus(Long dishId, UpdateDishStatusRequestDto request, Long ownerId) {
        var updated = dishServicePort.updateDishStatus(dishId, request.getActive(), ownerId);

        return dishResponseMapper.toResponse(updated);
    }

    @Override
    public PagedResponseDto<DishListItemResponseDto> listDishesByRestaurant(
            Long restaurantId,
            Long categoryId,
            Integer page,
            Integer size
    ) {
        var paged = dishServicePort.listDishesByRestaurant(restaurantId, categoryId, page, size);
        var pagedMapped = paged.mapTo(dishResponseMapper::toListItem);

        return PagedResponseDto.from(pagedMapped);
    }
}
