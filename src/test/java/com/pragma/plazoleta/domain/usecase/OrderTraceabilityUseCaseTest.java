package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.order.OrderNotBelongsToClientException;
import com.pragma.plazoleta.domain.exception.order.OrderNotFoundException;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderTraceability;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.IOrderTraceabilityPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTraceabilityUseCaseTest {

    @Mock
    private IOrderTraceabilityPort orderTraceabilityPort;

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @InjectMocks
    private OrderTraceabilityUseCase orderTraceabilityUseCase;

    private Order validOrder;
    private OrderTraceability expectedTraceability;

    private static final Long ORDER_ID = 42L;
    private static final Long CLIENT_ID = 5L;

    @BeforeEach
    void setUp() {
        validOrder = Order.builder()
                .id(ORDER_ID)
                .clientId(CLIENT_ID)
                .build();

        expectedTraceability = OrderTraceability.builder()
                .orderId(ORDER_ID)
                .transitions(List.of())
                .build();
    }

    @Test
    @DisplayName(
            "Should return order traceability when " +
            "the order exists and belongs to the caller client in find by order id for client"
    )
    void shouldReturnTraceabilityWhenOrderExistsAndBelongsToClientInFindByOrderIdForClient() {
        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.of(validOrder));
        when(orderTraceabilityPort.findByOrderId(ORDER_ID))
                .thenReturn(expectedTraceability);

        var result = orderTraceabilityUseCase.findByOrderIdForClient(ORDER_ID, CLIENT_ID);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(ORDER_ID);

        verify(orderPersistencePort).findById(ORDER_ID);
        verify(orderTraceabilityPort).findByOrderId(ORDER_ID);
    }

    @Test
    @DisplayName(
            "Should throw OrderNotFoundException when " +
            "the order does not exist in find by order id for client"
    )
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExistInFindByOrderIdForClient() {
        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderTraceabilityUseCase.findByOrderIdForClient(ORDER_ID, CLIENT_ID))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderPersistencePort).findById(ORDER_ID);
        verify(orderTraceabilityPort, never()).findByOrderId(any());
    }

    @Test
    @DisplayName(
            "Should throw OrderNotBelongsToClientException when " +
            "the caller client is not the owner of the order in find by order id for client"
    )
    void shouldThrowOrderNotBelongsToClientExceptionWhenCallerIsNotTheOwnerInFindByOrderIdForClient() {
        var otherClientId = 99L;

        when(orderPersistencePort.findById(ORDER_ID))
                .thenReturn(Optional.of(validOrder));

        assertThatThrownBy(() -> orderTraceabilityUseCase.findByOrderIdForClient(ORDER_ID, otherClientId))
                .isInstanceOf(OrderNotBelongsToClientException.class);

        verify(orderPersistencePort).findById(ORDER_ID);
        verify(orderTraceabilityPort, never()).findByOrderId(any());
    }
}