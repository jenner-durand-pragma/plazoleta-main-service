package com.pragma.plazoleta.domain.exception.restaurantemployee;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class EmployeeWithoutRestaurantException extends BusinessRuleException {

    private static final String ERROR_MESSAGE = "The employee is not assigned to any restaurant.";

    public EmployeeWithoutRestaurantException() {
        super(ERROR_MESSAGE);
    }
}
