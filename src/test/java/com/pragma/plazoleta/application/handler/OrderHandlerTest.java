package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.order.CreateOrderDishDto;
import com.pragma.plazoleta.application.dto.request.order.CreateOrderRequestDto;
import com.pragma.plazoleta.application.handler.impl.OrderHandler;
import com.pragma.plazoleta.application.mapper.IOrderRequestMapper;
import com.pragma.plazoleta.application.mapper.IOrderResponseMapper;
import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderHandlerTest {

    @Mock
    private IOrderServicePort orderServicePort;

    @Spy
    private IOrderRequestMapper orderRequestMapper = Mappers.getMapper(IOrderRequestMapper.class);

    @Spy
    private IOrderResponseMapper orderResponseMapper = Mappers.getMapper(IOrderResponseMapper.class);

    @InjectMocks
    private OrderHandler orderHandler;

    private CreateOrderRequestDto requestDto;
    private Order savedOrder;

    private static final Long CLIENT_ID = 5L;
    private static final Long RESTAURANT_ID = 10L;

    @BeforeEach
    void setUp() {
        requestDto = CreateOrderRequestDto.builder()
                .items(List.of(
                        CreateOrderDishDto.builder().dishId(1L).quantity(2).build()
                ))
                .build();

        var restaurant = Restaurant.builder()
                .id(RESTAURANT_ID)
                .name("Pizza Place")
                .build();

        var dish = Dish.builder()
                .id(1L)
                .name("Pineapple Pizza")
                .build();

        savedOrder = Order.builder()
                .id(42L)
                .clientId(CLIENT_ID)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .restaurant(restaurant)
                .items(List.of(
                        OrderDish.builder()
                                .orderId(42L)
                                .dishId(1L)
                                .dish(dish)
                                .quantity(2)
                                .build()
                ))
                .build();
    }

    @Test
    @DisplayName("Should create order successfully when data is valid in create order")
    void shouldCreateOrderSuccessfullyWhenDataIsValidInCreateOrder() {
        when(orderServicePort.createOrder(any(Order.class), eq(CLIENT_ID)))
                .thenReturn(savedOrder);

        var result = orderHandler.createOrder(RESTAURANT_ID, requestDto, CLIENT_ID);

        var orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderServicePort).createOrder(orderCaptor.capture(), eq(CLIENT_ID));
        var passedOrder = orderCaptor.getValue();

        assertThat(passedOrder.getRestaurant().getId()).isEqualTo(RESTAURANT_ID);
        assertThat(passedOrder.getItems()).hasSize(1);
        assertThat(passedOrder.getItems().get(0).getDishId()).isEqualTo(1L);
        assertThat(passedOrder.getItems().get(0).getDish().getId()).isEqualTo(1L);
        assertThat(passedOrder.getItems().get(0).getQuantity()).isEqualTo(2);

        verify(orderRequestMapper).toOrder(requestDto, RESTAURANT_ID);
        verify(orderResponseMapper).toResponse(savedOrder);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getClientId()).isEqualTo(CLIENT_ID);
        assertThat(result.getRestaurantId()).isEqualTo(RESTAURANT_ID);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getDishName()).isEqualTo("Pineapple Pizza");
    }
}
