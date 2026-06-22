package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.mapper.IOrderTraceabilityResponseMapper;
import com.pragma.plazoleta.domain.api.IOrderTraceabilityServicePort;
import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.model.OrderStateTraceability;
import com.pragma.plazoleta.domain.model.OrderTraceability;
import com.pragma.plazoleta.domain.model.OrderUserTraceability;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTraceabilityHandlerTest {

    @Mock
    private IOrderTraceabilityServicePort orderTraceabilityServicePort;

    @Spy
    private IOrderTraceabilityResponseMapper orderTraceabilityResponseMapper =
            Mappers.getMapper(IOrderTraceabilityResponseMapper.class);

    @InjectMocks
    private OrderTraceabilityHandler orderTraceabilityHandler;

    private OrderTraceability expectedOrderTraceability;

    private static final Long ORDER_ID = 42L;
    private static final Long CLIENT_ID = 5L;

    @BeforeEach
    void setUp() {
        var client = OrderUserTraceability.builder()
                .id(CLIENT_ID)
                .name("Jenner")
                .lastName("Durand")
                .email("jenner.durand@plazoleta.com")
                .build();

        var employee = OrderUserTraceability.builder()
                .id(1L)
                .name("Admin")
                .lastName("Plazoleta")
                .email("admin@plazoleta.com")
                .build();

        var transition = OrderStateTraceability.builder()
                .previousStatus(OrderStatus.PENDING)
                .newStatus(OrderStatus.IN_PREPARATION)
                .changedAt(LocalDateTime.of(2026, 6, 21, 10, 30))
                .employee(employee)
                .build();

        expectedOrderTraceability = OrderTraceability.builder()
                .orderId(ORDER_ID)
                .client(client)
                .transitions(List.of(transition))
                .build();
    }

    @Test
    @DisplayName(
            "Should return mapped traceability response when " +
            "data is valid in find by order id for client"
    )
    void shouldReturnMappedTraceabilityResponseWhenDataIsValidInFindByOrderIdForClient() {
        when(orderTraceabilityServicePort.findByOrderIdForClient(ORDER_ID, CLIENT_ID))
                .thenReturn(expectedOrderTraceability);

        var result = orderTraceabilityHandler.findByOrderIdForClient(ORDER_ID, CLIENT_ID);

        verify(orderTraceabilityServicePort).findByOrderIdForClient(ORDER_ID, CLIENT_ID);
        verify(orderTraceabilityResponseMapper).toResponse(expectedOrderTraceability);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(ORDER_ID);

        assertThat(result.getClient()).isNotNull();
        assertThat(result.getClient().getId())
                .isEqualTo(CLIENT_ID);
        assertThat(result.getClient().getName())
                .isEqualTo("Jenner");
        assertThat(result.getClient().getLastName())
                .isEqualTo("Durand");
        assertThat(result.getClient().getEmail())
                .isEqualTo("jenner.durand@plazoleta.com");

        assertThat(result.getTransitions()).hasSize(1);

        var mappedTransition = result.getTransitions().get(0);
        assertThat(mappedTransition.getPreviousStatus())
                .isEqualTo(OrderStatus.PENDING);
        assertThat(mappedTransition.getNewStatus())
                .isEqualTo(OrderStatus.IN_PREPARATION);
        assertThat(mappedTransition.getChangedAt())
                .isEqualTo(LocalDateTime.of(2026, 6, 21, 10, 30));

        assertThat(mappedTransition.getEmployee()).isNotNull();
        assertThat(mappedTransition.getEmployee().getId())
                .isEqualTo(1L);
        assertThat(mappedTransition.getEmployee().getName())
                .isEqualTo("Admin");
        assertThat(mappedTransition.getEmployee().getLastName())
                .isEqualTo("Plazoleta");
        assertThat(mappedTransition.getEmployee().getEmail())
                .isEqualTo("admin@plazoleta.com");
    }
}
