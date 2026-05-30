package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.enums.Roles;
import com.pragma.plazoleta.domain.exception.restaurant.NitAlreadyExistsException;
import com.pragma.plazoleta.domain.exception.restaurant.UserIsNotOwnerException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserInformationPort userValidationPort;

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        validateOwner(restaurant.getOwnerId());
        validateNitUniqueness(restaurant.getNit());

        return restaurantPersistencePort.save(restaurant);
    }

    private void validateOwner(Long ownerId) {
        var user = userValidationPort.getUserById(ownerId);

        if (user == null || !Roles.OWNER.getName().equals(user.getRoleName())) {
            throw new UserIsNotOwnerException();
        }
    }

    private void validateNitUniqueness(String nit) {
        var existsByNit = restaurantPersistencePort.existsByNit(nit);

        if (Boolean.TRUE.equals(existsByNit)) {
            throw new NitAlreadyExistsException();
        }
    }
}
