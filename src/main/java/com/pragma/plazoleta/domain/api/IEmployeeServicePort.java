package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.model.UserInformation;

public interface IEmployeeServicePort {

    RestaurantEmployee createEmployee(
            Long restaurantId,
            UserInformation userInformation,
            Long callerOwnerId
    );

}
