package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.exception.category.CategoryNotFoundException;
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
    public Dish createDish(Dish dish, Long ownerId) {
        var category = resolveCategory(dish.getCategory().getId());
        var restaurant = resolveRestaurant(dish.getRestaurant().getId());
        dish.setCategory(category);
        dish.setRestaurant(restaurant);

        dish.checkOwnership(ownerId);
        dish.setActive(true);

        return dishPersistencePort.save(dish);
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
}
