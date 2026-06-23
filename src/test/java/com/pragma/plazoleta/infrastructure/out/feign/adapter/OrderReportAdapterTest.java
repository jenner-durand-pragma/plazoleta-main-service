package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.pragma.plazoleta.infrastructure.out.feign.client.IOrderReportFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.dto.EmployeeEfficiencyFeignResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderEfficiencyFeignResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderStateUserInformationDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.PagedFeignResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.mapper.IOrderReportFeignMapper;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.jetbrains.annotations.NotNull;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderReportAdapterTest {

    @Mock
    private IOrderReportFeignClient orderReportFeignClient;

    @Spy
    private IOrderReportFeignMapper orderReportFeignMapper = Mappers.getMapper(IOrderReportFeignMapper.class);

    @InjectMocks
    private OrderReportAdapter orderReportAdapter;

    private static final Long RESTAURANT_ID = 10L;
    private static final Integer PAGE = 0;
    private static final Integer SIZE = 10;

    private OrderStateUserInformationDto clientResponseDto;
    private OrderStateUserInformationDto employeeResponseDto;

    @BeforeEach
    void setUp() {
        clientResponseDto = OrderStateUserInformationDto.builder()
                .id(10L)
                .name("Jenner")
                .lastName("Durand")
                .email("jenner.durand@plazoleta.com")
                .build();

        employeeResponseDto = OrderStateUserInformationDto.builder()
                .id(1L)
                .name("Admin")
                .lastName("Plazoleta")
                .email("admin@plazoleta.com")
                .build();
    }

    @Test
    @DisplayName(
            "Should fetch, map and return paged order efficiency when " +
                    "traceability-service responds successfully in get order efficiency"
    )
    void shouldFetchMapAndReturnPagedOrderEfficiencySuccessfullyInGetOrderEfficiency() {
        var orderEfficiencyDto = OrderEfficiencyFeignResponseDto.builder()
                .orderId(42L)
                .startedAt(LocalDateTime.now().minusMinutes(15))
                .endedAt(LocalDateTime.now())
                .durationMinutes(15.0)
                .client(clientResponseDto)
                .build();

        var pagedResponseDto = new PagedFeignResponseDto<OrderEfficiencyFeignResponseDto>();
        pagedResponseDto.setItems(List.of(orderEfficiencyDto));
        pagedResponseDto.setPage(PAGE);
        pagedResponseDto.setSize(SIZE);
        pagedResponseDto.setTotalElements(1L);
        pagedResponseDto.setTotalPages(1);

        when(orderReportFeignClient.getOrderEfficiency(RESTAURANT_ID, PAGE, SIZE))
                .thenReturn(pagedResponseDto);

        var result = orderReportAdapter.getOrderEfficiency(RESTAURANT_ID, PAGE, SIZE);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);

        var item = result.getItems().get(0);
        assertThat(item.getOrderId()).isEqualTo(42L);
        assertThat(item.getDurationMinutes()).isEqualTo(15.0);
        assertThat(item.getClient().getId()).isEqualTo(10L);
        assertThat(item.getClient().getName()).isEqualTo("Jenner");

        verify(orderReportFeignClient).getOrderEfficiency(RESTAURANT_ID, PAGE, SIZE);
        verify(orderReportFeignMapper).toModelOrder(any(OrderEfficiencyFeignResponseDto.class));
    }

    @Test
    @DisplayName(
            "Should rethrow FeignException when traceability-service returns " +
            "500 Internal Server Error in get order efficiency"
    )
    void shouldRethrowFeignExceptionOnInternalServerErrorInGetOrderEfficiency() {
        var request = Request.create(
                Request.HttpMethod.GET,
                "/api/v1/traceability/restaurants/10/efficiency",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        var serverErrorException = getInternalServerError(request);

        doThrow(serverErrorException)
                .when(orderReportFeignClient).getOrderEfficiency(RESTAURANT_ID, PAGE, SIZE);

        assertThatThrownBy(() -> orderReportAdapter.getOrderEfficiency(RESTAURANT_ID, PAGE, SIZE))
                .isInstanceOf(FeignException.InternalServerError.class);
    }

    @Test
    @DisplayName(
            "Should fetch, map and return paged employee ranking when " +
                    "traceability-service responds successfully in get employee ranking"
    )
    void shouldFetchMapAndReturnPagedEmployeeRankingSuccessfullyInGetEmployeeRanking() {
        var employeeRankingDto = EmployeeEfficiencyFeignResponseDto.builder()
                .employee(employeeResponseDto)
                .averageMinutes(12.5)
                .ordersHandled(20L)
                .build();

        var pagedResponseDto = new PagedFeignResponseDto<EmployeeEfficiencyFeignResponseDto>();
        pagedResponseDto.setItems(List.of(employeeRankingDto));
        pagedResponseDto.setPage(PAGE);
        pagedResponseDto.setSize(SIZE);
        pagedResponseDto.setTotalElements(1L);
        pagedResponseDto.setTotalPages(1);

        when(orderReportFeignClient.getEmployeeRanking(RESTAURANT_ID, PAGE, SIZE))
                .thenReturn(pagedResponseDto);

        var result = orderReportAdapter.getEmployeeRanking(RESTAURANT_ID, PAGE, SIZE);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);

        var item = result.getItems().get(0);
        assertThat(item.getEmployee().getId()).isEqualTo(1L);
        assertThat(item.getEmployee().getName()).isEqualTo("Admin");
        assertThat(item.getAverageMinutes()).isEqualTo(12.5);
        assertThat(item.getOrdersHandled()).isEqualTo(20L);

        verify(orderReportFeignClient).getEmployeeRanking(RESTAURANT_ID, PAGE, SIZE);
        verify(orderReportFeignMapper).toModelEmployee(any(EmployeeEfficiencyFeignResponseDto.class));
    }

    @Test
    @DisplayName(
            "Should rethrow FeignException when traceability-service returns " +
            "500 Internal Server Error in get employee ranking"
    )
    void shouldRethrowFeignExceptionOnInternalServerErrorInGetEmployeeRanking() {
        var request = Request.create(
                Request.HttpMethod.GET,
                "/api/v1/traceability/restaurants/10/employees-ranking",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        var serverErrorException = getInternalServerError(request);

        doThrow(serverErrorException)
                .when(orderReportFeignClient).getEmployeeRanking(RESTAURANT_ID, PAGE, SIZE);

        assertThatThrownBy(() -> orderReportAdapter.getEmployeeRanking(RESTAURANT_ID, PAGE, SIZE))
                .isInstanceOf(FeignException.InternalServerError.class);
    }

    private FeignException.@NotNull InternalServerError getInternalServerError(Request request) {
        return new FeignException.InternalServerError(
                "Internal Server Error",
                request,
                null,
                Collections.emptyMap()
        );
    }
}
