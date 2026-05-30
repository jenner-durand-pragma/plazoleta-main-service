package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IEmployeeServicePort;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurant.UserIsNotOwnerException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeUseCase implements IEmployeeServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    private final IUserInformationPort userInformationPort;

    @Override
    public RestaurantEmployee createEmployee(
            Long restaurantId,
            UserInformation userInformation,
            Long ownerId
    ) {
        var restaurant = resolveRestaurant(restaurantId);
        validateOwnership(restaurant, ownerId);

        var userId = userInformationPort.createEmployee(userInformation);
        var restaurantEmployee = RestaurantEmployee.builder()
                .userId(userId)
                .restaurantId(restaurantId)
                .build();

        return restaurantEmployeePersistencePort.save(restaurantEmployee);
    }

    private Restaurant resolveRestaurant(Long restaurantId) {
        var restaurant = restaurantPersistencePort.findById(restaurantId);
        if (restaurant == null) {
            throw new RestaurantNotFoundException(restaurantId);
        }

        return restaurant;
    }

    private void validateOwnership(Restaurant restaurant, Long ownerId) {
        if (!restaurant.getOwnerId().equals(ownerId)) {

            throw new UserIsNotOwnerException();
        }
    }
}
