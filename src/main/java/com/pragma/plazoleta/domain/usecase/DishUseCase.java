package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.exception.category.CategoryNotFoundException;
import com.pragma.plazoleta.domain.exception.dish.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;

    @Override
    public Dish createDish(Long restaurantId, Dish dish, Long ownerId) {
        var category = resolveCategory(dish.getCategory().getId());
        var restaurant = resolveRestaurant(restaurantId);
        restaurant.checkOwnership(ownerId);

        dish.setCategory(category);
        dish.setRestaurant(restaurant);
        dish.setActive(true);

        return dishPersistencePort.save(dish);
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

        return dishPersistencePort.save(dish);
    }

    @Override
    public Dish updateDishStatus(Long dishId, Boolean active, Long ownerId) {
        var dish = resolveDish(dishId);

        dish.getRestaurant().checkOwnership(ownerId);
        dish.setActive(active);

        return dishPersistencePort.save(dish);
    }

    @Override
    public PagedResult<Dish> listDishesByRestaurant(Long restaurantId, Long categoryId, Integer page, Integer size) {
        PagedResult.validatePagination(page, size);

        var restaurant = resolveRestaurant(restaurantId);

        return dishPersistencePort.findActiveByRestaurantAndCategoryPaginated(
                restaurant.getId(),
                categoryId,
                page,
                size
        );
    }

    private Category resolveCategory(Long categoryId) {
        var category = categoryPersistencePort.findById(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException(categoryId);
        }

        return category;
    }

    private Restaurant resolveRestaurant(Long restaurantId) {
        var restaurant = restaurantPersistencePort.findById(restaurantId);
        if (restaurant == null) {
            throw new RestaurantNotFoundException(restaurantId);
        }

        return restaurant;
    }

    private Dish resolveDish(Long dishId) {
        var dish = dishPersistencePort.findById(dishId);
        if (dish == null) {
            throw new DishNotFoundException(dishId);
        }

        return dish;
    }
}
