package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.exception.category.CategoryNotFoundException;
import com.pragma.plazoleta.domain.exception.dish.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.service.ICategoryCacheService;
import com.pragma.plazoleta.domain.service.IDishCacheService;
import com.pragma.plazoleta.domain.service.IRestaurantCacheService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DishUseCase implements IDishServicePort {

    private final IDishCacheService dishCacheService;
    private final IRestaurantCacheService restaurantCacheService;
    private final ICategoryCacheService categoryCacheService;

    @Override
    public Dish createDish(Dish dish, Long ownerId) {
        var category = resolveCategory(dish.getCategory().getId());
        var restaurant = resolveRestaurant(dish.getRestaurant().getId());
        restaurant.checkOwnership(ownerId);

        dish.setCategory(category);
        dish.setRestaurant(restaurant);
        dish.setActive(true);

        return dishCacheService.saveDish(dish);
    }

    @Override
    public Dish updateDish(Long dishId, Integer price, String description, Long ownerId) {
        var dish = resolveDish(dishId);
        dish.getRestaurant().checkOwnership(ownerId);

        if (price != null) {
            dish.setPrice(price);
        }

        if (description != null && !description.trim().isEmpty()) {
            dish.setDescription(description);
        }

        return dishCacheService.saveDish(dish);
    }

    @Override
    public Dish updateDishStatus(Long dishId, Boolean active, Long ownerId) {
        var dish = resolveDish(dishId);

        dish.getRestaurant().checkOwnership(ownerId);
        dish.setActive(active);

        return dishCacheService.saveDish(dish);
    }

    @Override
    public PagedResult<Dish> listDishesByRestaurant(Long restaurantId, Long categoryId, Integer page, Integer size) {
        PagedResult.validatePagination(page, size);

        var restaurant = resolveRestaurant(restaurantId);

        return dishCacheService.listDishes(
                restaurant.getId(),
                categoryId,
                page,
                size
        );
    }

    private Category resolveCategory(Long categoryId) {
        var category = categoryCacheService.getCategoryById(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException(categoryId);
        }

        return category;
    }

    private Restaurant resolveRestaurant(Long restaurantId) {
        var restaurant = restaurantCacheService.getRestaurantById(restaurantId);
        if (restaurant == null) {
            throw new RestaurantNotFoundException(restaurantId);
        }

        return restaurant;
    }

    private Dish resolveDish(Long dishId) {
        var dish = dishCacheService.getDishById(dishId);
        if (dish == null) {
            throw new DishNotFoundException(dishId);
        }

        return dish;
    }
}
