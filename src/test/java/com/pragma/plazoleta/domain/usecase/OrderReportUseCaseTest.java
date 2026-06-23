package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantOwnershipException;
import com.pragma.plazoleta.domain.model.EmployeeEfficiency;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.domain.model.OrderUser;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IOrderReportQueryPort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderReportUseCaseTest {

    @Mock
    private IOrderReportQueryPort orderReportQueryPort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @InjectMocks
    private OrderReportUseCase orderReportUseCase;

    private static final Long RESTAURANT_ID = 10L;
    private static final Long OWNER_ID = 2L;
    private static final Long ANOTHER_OWNER_ID = 99L;
    private static final Integer VALID_PAGE = 0;
    private static final Integer VALID_SIZE = 10;

    private Restaurant ownedRestaurant;
    private PagedResult<OrderEfficiency> orderEfficiencyPagedResult;
    private PagedResult<EmployeeEfficiency> employeeEfficiencyPagedResult;

    @BeforeEach
    void setUp() {
        ownedRestaurant = Restaurant.builder()
                .id(RESTAURANT_ID)
                .ownerId(OWNER_ID)
                .name("Pizza Place")
                .build();

        var client = OrderUser.builder()
                .id(5L)
                .name("Jenner")
                .lastName("Durand")
                .email("jenner.durand@plazoleta.com")
                .build();

        var employee = OrderUser.builder()
                .id(6L)
                .name("Admin")
                .lastName("Plazoleta")
                .email("admin@plazoleta.com")
                .build();

        var orderEfficiency = OrderEfficiency.builder()
                .orderId(42L)
                .startedAt(LocalDateTime.now().minusMinutes(15))
                .endedAt(LocalDateTime.now())
                .durationMinutes(15.0)
                .client(client)
                .build();

        var employeeEfficiency = EmployeeEfficiency.builder()
                .employee(employee)
                .averageMinutes(12.5)
                .ordersHandled(20L)
                .build();

        orderEfficiencyPagedResult = PagedResult.of(
                List.of(orderEfficiency), VALID_PAGE, VALID_SIZE, 1L, 1
        );

        employeeEfficiencyPagedResult = PagedResult.of(
                List.of(employeeEfficiency), VALID_PAGE, VALID_SIZE, 1L, 1
        );
    }

    @Test
    @DisplayName(
            "Should return paged order efficiency when " +
            "owner owns the restaurant in get order efficiency"
    )
    void shouldReturnPagedOrderEfficiencyWhenOwnerOwnsTheRestaurantInGetOrderEfficiency() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(ownedRestaurant);
        when(orderReportQueryPort.getOrderEfficiency(RESTAURANT_ID, VALID_PAGE, VALID_SIZE))
                .thenReturn(orderEfficiencyPagedResult);

        var result = orderReportUseCase.getOrderEfficiency(RESTAURANT_ID, OWNER_ID, VALID_PAGE, VALID_SIZE);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getDurationMinutes()).isEqualTo(15.0);
        verify(orderReportQueryPort).getOrderEfficiency(RESTAURANT_ID, VALID_PAGE, VALID_SIZE);
    }

    @Test
    @DisplayName(
            "Should throw RestaurantNotFoundException when " +
            "the restaurant does not exist in get order efficiency"
    )
    void shouldThrowRestaurantNotFoundExceptionWhenTheRestaurantDoesNotExistInGetOrderEfficiency() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(null);

        assertThatThrownBy(() -> orderReportUseCase.getOrderEfficiency(RESTAURANT_ID, OWNER_ID, VALID_PAGE, VALID_SIZE))
                .isInstanceOf(RestaurantNotFoundException.class);

        verify(orderReportQueryPort, never()).getOrderEfficiency(any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName(
            "Should throw RestaurantOwnershipException when " +
            "the caller is not the owner in get order efficiency"
    )
    void shouldThrowRestaurantOwnershipExceptionWhenTheCallerIsNotTheOwnerInGetOrderEfficiency() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(ownedRestaurant);

        assertThatThrownBy(() -> orderReportUseCase
                .getOrderEfficiency(RESTAURANT_ID, ANOTHER_OWNER_ID, VALID_PAGE, VALID_SIZE))
                .isInstanceOf(RestaurantOwnershipException.class);

        verify(orderReportQueryPort, never()).getOrderEfficiency(any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName(
            "Should return employee ranking when " +
            "owner owns the restaurant in get employee ranking"
    )
    void shouldReturnEmployeeRankingWhenOwnerOwnsTheRestaurantInGetEmployeeRanking() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(ownedRestaurant);
        when(orderReportQueryPort.getEmployeeRanking(RESTAURANT_ID, VALID_PAGE, VALID_SIZE))
                .thenReturn(employeeEfficiencyPagedResult);

        var result = orderReportUseCase.getEmployeeRanking(RESTAURANT_ID, OWNER_ID, VALID_PAGE, VALID_SIZE);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getAverageMinutes()).isEqualTo(12.5);
        verify(orderReportQueryPort).getEmployeeRanking(RESTAURANT_ID, VALID_PAGE, VALID_SIZE);
    }

    @Test
    @DisplayName(
            "Should throw RestaurantNotFoundException when " +
            "the restaurant does not exist in get employee ranking"
    )
    void shouldThrowRestaurantNotFoundExceptionWhenTheRestaurantDoesNotExistInGetEmployeeRanking() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(null);

        assertThatThrownBy(() -> orderReportUseCase.getEmployeeRanking(RESTAURANT_ID, OWNER_ID, VALID_PAGE, VALID_SIZE))
                .isInstanceOf(RestaurantNotFoundException.class);

        verify(orderReportQueryPort, never()).getEmployeeRanking(any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName(
            "Should throw RestaurantOwnershipException when " +
            "the caller is not the owner in get employee ranking"
    )
    void shouldThrowRestaurantOwnershipExceptionWhenTheCallerIsNotTheOwnerInGetEmployeeRanking() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(ownedRestaurant);

        assertThatThrownBy(() -> orderReportUseCase
                .getEmployeeRanking(RESTAURANT_ID, ANOTHER_OWNER_ID, VALID_PAGE, VALID_SIZE))
                .isInstanceOf(RestaurantOwnershipException.class);

        verify(orderReportQueryPort, never()).getEmployeeRanking(any(), anyInt(), anyInt());
    }
}
