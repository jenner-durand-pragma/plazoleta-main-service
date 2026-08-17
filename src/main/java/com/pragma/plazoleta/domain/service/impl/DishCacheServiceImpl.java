package com.pragma.plazoleta.domain.service.impl;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.service.ICategoryCacheService;
import com.pragma.plazoleta.domain.service.IDishCacheService;
import com.pragma.plazoleta.domain.service.IRestaurantCacheService;
import com.pragma.plazoleta.domain.spi.IDishCachePort;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DishCacheServiceImpl implements IDishCacheService {

    private final IDishCachePort dishCachePort;
    private final IDishPersistencePort dishPersistencePort;

    private final ICategoryCacheService categoryCacheService;
    private final IRestaurantCacheService restaurantCacheService;

    @Override
    public Dish getDishById(Long id) {
        var dish = dishCachePort.getDishById(id)
                .orElseGet(() -> dishPersistencePort.findById(id));
        dish.setRestaurant(restaurantCacheService.getRestaurantById(dish.getRestaurant().getId()));
        dish.setCategory(categoryCacheService.getCategoryById(dish.getCategory().getId()));

        return dish;
    }

    @Override
    public Dish saveDish(Dish dish) {
        var dishSaved = dishPersistencePort.save(dish);
        dishCachePort.saveDish(dishSaved);
        dishCachePort.deleteDishPagesCached();

        return dishSaved;
    }

    @Override
    public PagedResult<Dish> listDishes(Long restaurantId, Long categoryId, Integer page, Integer size) {
        return dishCachePort.getDishList(restaurantId, categoryId, page, size)
                .orElseGet(() -> dishPersistencePort.findActiveByRestaurantAndCategoryPaginated(
                        restaurantId,
                        categoryId,
                        page,
                        size
                ));
    }
}
