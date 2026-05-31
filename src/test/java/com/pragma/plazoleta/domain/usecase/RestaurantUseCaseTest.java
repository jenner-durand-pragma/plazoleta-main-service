package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.restaurant.NitAlreadyExistsException;
import com.pragma.plazoleta.domain.exception.restaurant.UserIsNotOwnerException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserInformationPort userInformationPort;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    private Restaurant validRestaurant;
    private UserInformation ownerUser;

    @BeforeEach
    void setUp() {
        validRestaurant = Restaurant.builder()
                .name("Pizza Place")
                .address("Example Street 123")
                .ownerId(5L)
                .phone("+573005698325")
                .logoUrl("https://logo.example.com/image.png")
                .nit("9001234567")
                .build();

        ownerUser = UserInformation.builder()
                .id(5L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .roleName("OWNER")
                .build();
    }

    @Test
    @DisplayName("Should create a restaurant successfully when owner is valid and NIT is unique in create restaurant")
    void shouldCreateRestaurantSuccessfullyWhenAllDataIsValidInCreateRestaurant() {
        when(userInformationPort.getUserById(5L))
                .thenReturn(ownerUser);
        when(restaurantPersistencePort.existsByNit("9001234567"))
                .thenReturn(false);
        when(restaurantPersistencePort.save(any(Restaurant.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = restaurantUseCase.createRestaurant(validRestaurant);

        assertThat(result).isNotNull();
        assertThat(result.getNit()).isEqualTo("9001234567");

        verify(restaurantPersistencePort).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should throw UserIsNotOwnerException when user role is not OWNER in create restaurant")
    void shouldThrowExceptionWhenUserRoleIsNotOwnerInCreateRestaurant() {
        var client = UserInformation.builder()
                .id(5L)
                .name("Client")
                .lastName("Test")
                .documentNumber("1234567890")
                .phone("+573005698325")
                .email("client@plazoleta.com")
                .roleName("CLIENT")
                .build();
        when(userInformationPort.getUserById(5L)).thenReturn(client);

        assertThatThrownBy(() -> restaurantUseCase.createRestaurant(validRestaurant))
                .isInstanceOf(UserIsNotOwnerException.class);

        verify(restaurantPersistencePort, never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should throw UserIsNotOwnerException when user does not exist in create restaurant")
    void shouldThrowExceptionWhenUserDoesNotExistInCreateRestaurant() {
        when(userInformationPort.getUserById(5L)).thenReturn(null);

        assertThatThrownBy(() -> restaurantUseCase.createRestaurant(validRestaurant))
                .isInstanceOf(UserIsNotOwnerException.class);

        verify(restaurantPersistencePort, never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Should throw NitAlreadyExistsException when NIT is duplicated in create restaurant")
    void shouldThrowExceptionWhenNitAlreadyExistsInCreateRestaurant() {
        when(userInformationPort.getUserById(5L)).thenReturn(ownerUser);
        when(restaurantPersistencePort.existsByNit("9001234567")).thenReturn(true);

        assertThatThrownBy(() -> restaurantUseCase.createRestaurant(validRestaurant))
                .isInstanceOf(NitAlreadyExistsException.class);

        verify(restaurantPersistencePort, never()).save(any(Restaurant.class));
    }
}
