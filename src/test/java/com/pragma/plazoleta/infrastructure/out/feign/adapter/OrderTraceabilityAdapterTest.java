package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.model.OrderState;
import com.pragma.plazoleta.domain.model.OrderTraceability;
import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import com.pragma.plazoleta.infrastructure.out.feign.client.IOrderTraceabilityFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderStateRequestDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderStateTraceabilityResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderStateUserInformationDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderTraceabilityResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.mapper.IOrderTraceabilityFeignMapper;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.jetbrains.annotations.NotNull;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTraceabilityAdapterTest {

    @Mock
    private IOrderTraceabilityFeignClient orderTraceabilityFeignClient;

    @Mock
    private IUserInformationPort userInformationPort;

    @Spy
    private IOrderTraceabilityFeignMapper orderTraceabilityFeignMapper =
            Mappers.getMapper(IOrderTraceabilityFeignMapper.class);

    @InjectMocks
    private OrderTraceabilityAdapter orderTraceabilityAdapter;

    private UserInformation clientUser;
    private UserInformation employeeUser;

    @BeforeEach
    void setUp() {
        clientUser = UserInformation.builder()
                .id(10L)
                .name("Jenner")
                .lastName("Durand")
                .email("jenner.durand@plazoleta.com")
                .build();

        employeeUser = UserInformation.builder()
                .id(1L)
                .name("Admin")
                .lastName("Plazoleta")
                .email("admin@plazoleta.com")
                .build();
    }

    @Test
    @DisplayName(
            "Should resolve users, map correctly and send request when " +
            "traceability-service responds successfully in save state"
    )
    void shouldResolveUsersAndSendRequestSuccessfullyInSaveState() {
        var orderState = OrderState.builder()
                .orderId(42L)
                .restaurantId(10L)
                .previousStatus(OrderStatus.IN_PREPARATION)
                .newStatus(OrderStatus.READY)
                .changedAt(LocalDateTime.now())
                .clientId(10L)
                .employeeId(1L)
                .build();

        when(userInformationPort.getUserById(10L)).thenReturn(clientUser);
        when(userInformationPort.getUserById(1L)).thenReturn(employeeUser);

        orderTraceabilityAdapter.saveState(orderState);

        var captor = ArgumentCaptor.forClass(OrderStateRequestDto.class);
        verify(orderTraceabilityFeignClient).saveState(captor.capture());

        var capturedRequest = captor.getValue();
        assertThat(capturedRequest.getOrderId()).isEqualTo(42L);
        assertThat(capturedRequest.getClient().getEmail()).isEqualTo("jenner.durand@plazoleta.com");
        assertThat(capturedRequest.getEmployee().getEmail()).isEqualTo("admin@plazoleta.com");
    }

    @Test
    @DisplayName(
            "Should resolve only client and send request with null employee when" +
            "employeeId is absent in save state"
    )
    void shouldResolveOnlyClientAndSendRequestWithNullEmployeeInSaveState() {
        var orderState = OrderState.builder()
                .orderId(42L)
                .restaurantId(10L)
                .previousStatus(null)
                .newStatus(OrderStatus.PENDING)
                .changedAt(LocalDateTime.now())
                .clientId(10L)
                .employeeId(null)
                .build();

        when(userInformationPort.getUserById(10L)).thenReturn(clientUser);

        orderTraceabilityAdapter.saveState(orderState);

        var captor = ArgumentCaptor.forClass(OrderStateRequestDto.class);
        verify(orderTraceabilityFeignClient).saveState(captor.capture());

        var capturedRequest = captor.getValue();
        assertThat(capturedRequest.getOrderId()).isEqualTo(42L);
        assertThat(capturedRequest.getClient().getName()).isEqualTo("Jenner");
        assertThat(capturedRequest.getEmployee()).isNull();
    }

    @Test
    @DisplayName("Should rethrow FeignException when traceability-service returns 400 Bad Request")
    void shouldRethrowFeignExceptionOnBadRequest() {
        var orderState = OrderState.builder()
                .orderId(42L)
                .clientId(10L)
                .employeeId(1L)
                .build();

        when(userInformationPort.getUserById(10L)).thenReturn(clientUser);
        when(userInformationPort.getUserById(1L)).thenReturn(employeeUser);

        var request = Request.create(
                Request.HttpMethod.POST,
                "/api/v1/traceability/orders/states",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        var badRequestException = getBadRequest(request);

        doThrow(badRequestException)
                .when(orderTraceabilityFeignClient)
                .saveState(any(OrderStateRequestDto.class));

        assertThatThrownBy(() -> orderTraceabilityAdapter.saveState(orderState))
                .isInstanceOf(FeignException.BadRequest.class);
    }

    @Test
    @DisplayName("Should rethrow FeignException when traceability-service returns 500 Internal Server Error")
    void shouldRethrowFeignExceptionOnInternalServerError() {
        var orderState = OrderState.builder()
                .orderId(42L)
                .clientId(10L)
                .employeeId(null)
                .build();

        when(userInformationPort.getUserById(10L)).thenReturn(clientUser);

        var request = Request.create(
                Request.HttpMethod.POST,
                "/api/v1/traceability/orders/states",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        var serverErrorException = getInternalServerError(request);

        doThrow(serverErrorException)
                .when(orderTraceabilityFeignClient).saveState(any(OrderStateRequestDto.class));

        assertThatThrownBy(() -> orderTraceabilityAdapter.saveState(orderState))
                .isInstanceOf(FeignException.InternalServerError.class);
    }

    @Test
    @DisplayName(
            "Should map and return order traceability model when " +
            "traceability-service responds successfully in find by order id"
    )
    void shouldMapAndReturnModelWhenServiceRespondsSuccessfullyInFindByOrderId() {
        var employeeTransitionDto = OrderStateUserInformationDto.builder()
                .id(2L)
                .name("Employee")
                .lastName("Second")
                .email("employee.2@plazoleta.com")
                .build();
        var clientDto = OrderStateUserInformationDto.builder()
                .id(3L)
                .name("Client")
                .lastName("First")
                .email("client.1@plazoleta.com")
                .build();
        var transitionDto = OrderStateTraceabilityResponseDto.builder()
                .previousStatus(OrderStatus.PENDING)
                .newStatus(OrderStatus.IN_PREPARATION)
                .employee(employeeTransitionDto)
                .changedAt(LocalDateTime.of(2026, 6, 21, 10, 0))
                .build();

        var responseDto = OrderTraceabilityResponseDto.builder()
                .orderId(42L)
                .client(clientDto)
                .transitions(List.of(transitionDto))
                .build();

        when(orderTraceabilityFeignClient.findByOrderId(42L)).thenReturn(responseDto);

        var result = orderTraceabilityAdapter.findByOrderId(42L);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(42L);
        assertThat(result.getTransitions()).hasSize(1);
        assertThat(result.getClient().getId()).isEqualTo(3L);

        var mappedTransition = result.getTransitions().get(0);
        assertThat(mappedTransition.getPreviousStatus())
                .isEqualTo(OrderStatus.PENDING);
        assertThat(mappedTransition.getEmployee().getId())
                .isEqualTo(2L);
        assertThat(mappedTransition.getNewStatus())
                .isEqualTo(OrderStatus.IN_PREPARATION);
        assertThat(mappedTransition.getChangedAt())
                .isEqualTo(LocalDateTime.of(2026, 6, 21, 10, 0));

        verify(orderTraceabilityFeignClient).findByOrderId(42L);
        verify(orderTraceabilityFeignMapper).toModel(responseDto);
    }

    @Test
    @DisplayName(
            "Should return null when " +
            "traceability-service returns 404 Not Found in find by order id"
    )
    void shouldReturnNullWhenServiceReturnsNotFoundInFindByOrderId() {
        var orderId = 42L;
        var request = Request.create(
                Request.HttpMethod.GET,
                "/api/v1/traceability/orders/42",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        var notFoundException = getNotFound(request, orderId);

        doThrow(notFoundException).when(orderTraceabilityFeignClient).findByOrderId(orderId);

        var result = orderTraceabilityAdapter.findByOrderId(orderId);

        assertThat(result).isNull();
        verify(orderTraceabilityFeignClient).findByOrderId(orderId);
    }

    private FeignException.@NotNull BadRequest getBadRequest(Request request) {
        var jsonBody = "{\n" +
                "    \"message\": \"Invalid order state transition\"\n" +
                "}";

        return new FeignException.BadRequest(
                "Bad Request",
                request,
                jsonBody.getBytes(StandardCharsets.UTF_8),
                Collections.emptyMap()
        );
    }

    private FeignException.@NotNull InternalServerError getInternalServerError(Request request) {
        return new FeignException.InternalServerError(
                "Internal Server Error",
                request,
                null,
                Collections.emptyMap()
        );
    }

    private FeignException.@NotNull NotFound getNotFound(Request request, Long orderId) {
        var jsonBody = String.format(
                "{\n" +
                        "    \"message\": \"No traceability records found for order: %d\"\n" +
                        "}",
                orderId
        );

        return new FeignException.NotFound(
                "Not Found",
                request,
                jsonBody.getBytes(StandardCharsets.UTF_8),
                Collections.emptyMap()
        );
    }
}