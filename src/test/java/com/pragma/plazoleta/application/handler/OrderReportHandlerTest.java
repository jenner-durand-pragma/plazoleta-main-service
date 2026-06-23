package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.handler.impl.OrderReportHandler;
import com.pragma.plazoleta.application.mapper.IOrderReportMapper;
import com.pragma.plazoleta.domain.api.IOrderReportServicePort;
import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.EmployeeEfficiency;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.domain.model.OrderUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderReportHandlerTest {

    @Mock
    private IOrderReportServicePort orderReportServicePort;

    @Spy
    private IOrderReportMapper orderReportMapper = Mappers.getMapper(IOrderReportMapper.class);

    @InjectMocks
    private OrderReportHandler orderReportHandler;

    private static final Long RESTAURANT_ID = 10L;
    private static final Long OWNER_ID = 2L;
    private static final Integer PAGE = 0;
    private static final Integer SIZE = 10;

    @Test
    @DisplayName("Should retrieve mapped order efficiency in get order efficiency")
    void shouldReturnMappedOrderEfficiencyInGetOrderEfficiency() {
        var orderId = 42L;
        var client = OrderUser.builder()
                .id(5L)
                .name("Jenner")
                .lastName("Durand")
                .email("jenner.durand@plazoleta.com")
                .build();

        var orderEfficiency = OrderEfficiency.builder()
                .orderId(orderId)
                .startedAt(LocalDateTime.now().minusMinutes(15))
                .endedAt(LocalDateTime.now())
                .durationMinutes(15.0)
                .client(client)
                .build();

        var pagedResult = PagedResult.of(List.of(orderEfficiency), PAGE, SIZE, 1L, 1);

        when(orderReportServicePort.getOrderEfficiency(RESTAURANT_ID, OWNER_ID, PAGE, SIZE))
                .thenReturn(pagedResult);

        var result = orderReportHandler.getOrderEfficiency(RESTAURANT_ID, OWNER_ID, PAGE, SIZE);

        verify(orderReportServicePort).getOrderEfficiency(RESTAURANT_ID, OWNER_ID, PAGE, SIZE);
        verify(orderReportMapper).toResponseOrder(any(OrderEfficiency.class));

        assertThat(result).isNotNull();

        var firstItem = result.getItems().get(0);
        assertThat(firstItem.getOrderId()).isEqualTo(orderId);
        assertThat(firstItem.getDurationMinutes()).isEqualTo(15.0);
        assertThat(firstItem.getClient().getName()).isEqualTo("Jenner");
    }

    @Test
    @DisplayName("Should retrieve mapped employee ranking in get employee ranking")
    void shouldReturnMappedEmployeeRankingInGetEmployeeRanking() {
        var employee = OrderUser.builder()
                .id(6L)
                .name("Admin")
                .lastName("Plazoleta")
                .email("admin@plazoleta.com")
                .build();

        var employeeEfficiency = EmployeeEfficiency.builder()
                .employee(employee)
                .averageMinutes(12.5)
                .ordersHandled(20L)
                .build();

        var pagedResult = PagedResult.of(List.of(employeeEfficiency), PAGE, SIZE, 1L, 1);

        when(orderReportServicePort.getEmployeeRanking(RESTAURANT_ID, OWNER_ID, PAGE, SIZE))
                .thenReturn(pagedResult);

        var result = orderReportHandler.getEmployeeRanking(RESTAURANT_ID, OWNER_ID, PAGE, SIZE);

        verify(orderReportServicePort).getEmployeeRanking(RESTAURANT_ID, OWNER_ID, PAGE, SIZE);
        verify(orderReportMapper).toResponseEmployee(any(EmployeeEfficiency.class));

        assertThat(result).isNotNull();

        var firstItem = result.getItems().get(0);
        assertThat(firstItem.getEmployee().getId()).isEqualTo(6L);
        assertThat(firstItem.getAverageMinutes()).isEqualTo(12.5);
        assertThat(firstItem.getOrdersHandled()).isEqualTo(20L);
    }
}
