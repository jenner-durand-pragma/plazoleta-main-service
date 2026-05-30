package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.restaurant.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurant.UserIsNotOwnerException;
import com.pragma.plazoleta.domain.exception.restaurantemployee.EmployeeAlreadyAssignedException;
import com.pragma.plazoleta.domain.exception.restaurantemployee.RemoteUserConflictException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IRestaurantEmployeePersistencePort employeePersistencePort;

    @Mock
    private IUserInformationPort userInformationPort;

    @InjectMocks
    private EmployeeUseCase employeeUseCase;

    private UserInformation registration;
    private Restaurant restaurant;

    private static final Long RESTAURANT_ID = 10L;
    private static final Long CALLER_OWNER_ID = 2L;
    private static final Long REMOTE_USER_ID = 99L;

    @BeforeEach
    void setUp() {
        registration = UserInformation.builder()
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .build();

        restaurant = Restaurant.builder()
                .id(RESTAURANT_ID)
                .name("Pizza Place")
                .address("Example Street 123")
                .ownerId(5L)
                .phone("+573005698325")
                .logoUrl("https://logo.example.com/image.png")
                .nit("9001234567")
                .ownerId(CALLER_OWNER_ID)
                .build();
    }

    @Test
    @DisplayName("Should create employee user and persist the restaurant association")
    void shouldCreateEmployeeSuccessfully() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID))
                .thenReturn(restaurant);
        when(userInformationPort.createEmployee(registration))
                .thenReturn(REMOTE_USER_ID);
        when(employeePersistencePort.existsByUserId(REMOTE_USER_ID))
                .thenReturn(false);
        when(employeePersistencePort.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = employeeUseCase.createEmployee(RESTAURANT_ID, registration, CALLER_OWNER_ID);

        var captor = ArgumentCaptor.forClass(RestaurantEmployee.class);
        verify(employeePersistencePort).save(captor.capture());

        assertThat(captor.getValue().getUserId()).isEqualTo(REMOTE_USER_ID);
        assertThat(captor.getValue().getRestaurantId()).isEqualTo(RESTAURANT_ID);

        assertThat(result.getUserId()).isEqualTo(REMOTE_USER_ID);
        assertThat(result.getRestaurantId()).isEqualTo(RESTAURANT_ID);
    }

    @Test
    @DisplayName("Should throw RestaurantNotFoundException when restaurant does not exist in create employee")
    void shouldThrowWhenRestaurantMissingInCreateEmployee() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(null);

        assertThatThrownBy(() ->
                employeeUseCase.createEmployee(RESTAURANT_ID, registration, CALLER_OWNER_ID))
                .isInstanceOf(RestaurantNotFoundException.class);

        verify(userInformationPort, never()).createEmployee(any());
        verify(employeePersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserIsNotOwnerException when caller is not the restaurant owner in create employee")
    void shouldThrowWhenCallerIsNotOwnerInCreateEmployee() {
        var otherRestaurant = new Restaurant();
        otherRestaurant.setId(RESTAURANT_ID);
        otherRestaurant.setOwnerId(99L);
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(otherRestaurant);

        assertThatThrownBy(() ->
                employeeUseCase.createEmployee(RESTAURANT_ID, registration, CALLER_OWNER_ID))
                .isInstanceOf(UserIsNotOwnerException.class);

        verify(userInformationPort, never()).createEmployee(any());
        verify(employeePersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate RemoteUserConflictException from users-service in create employee")
    void shouldPropagateConflictInCreateEmployee() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(restaurant);
        when(userInformationPort.createEmployee(registration))
                .thenThrow(new RemoteUserConflictException("Email already exists"));

        assertThatThrownBy(() ->
                employeeUseCase.createEmployee(RESTAURANT_ID, registration, CALLER_OWNER_ID))
                .isInstanceOf(RemoteUserConflictException.class);

        verify(employeePersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EmployeeAlreadyAssignedException when user is already an employee in create employee")
    void shouldThrowWhenAlreadyAssignedInCreateEmployee() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(restaurant);
        when(userInformationPort.createEmployee(registration)).thenReturn(REMOTE_USER_ID);
        when(employeePersistencePort.existsByUserId(REMOTE_USER_ID)).thenReturn(true);

        assertThatThrownBy(() ->
                employeeUseCase.createEmployee(RESTAURANT_ID, registration, CALLER_OWNER_ID))
                .isInstanceOf(EmployeeAlreadyAssignedException.class);

        verify(employeePersistencePort, never()).save(any());
    }
}
