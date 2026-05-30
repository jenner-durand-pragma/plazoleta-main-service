package com.pragma.plazoleta.domain.exception.restaurantemployee;

import com.pragma.plazoleta.domain.exception.ConflictException;

public class EmployeeAlreadyAssignedException extends ConflictException {

  private static final String ERROR_MESSAGE = "This user is already assigned as an employee.";

  public EmployeeAlreadyAssignedException() {
    super(ERROR_MESSAGE);
  }
}
