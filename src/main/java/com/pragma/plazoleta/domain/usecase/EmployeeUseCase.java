package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IEmployeeServicePort;
import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeUseCase implements IEmployeeServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IRestaurantEmployeePersistencePort employeePersistencePort;
    private final IUserInformationPort userRegistrationPort;

    @Override
    public RestaurantEmployee createEmployee(
            Long restaurantId,
            UserInformation userInformation,
            Long callerOwnerId
    ) {
        return null;
    }
}
