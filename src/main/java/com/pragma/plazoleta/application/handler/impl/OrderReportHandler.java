package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.orderreport.EmployeeRankingResponseDto;
import com.pragma.plazoleta.application.dto.response.orderreport.OrderEfficiencyResponseDto;
import com.pragma.plazoleta.application.handler.IOrderReportHandler;
import com.pragma.plazoleta.application.mapper.IOrderReportMapper;
import com.pragma.plazoleta.domain.api.IOrderReportServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderReportHandler implements IOrderReportHandler {

    private final IOrderReportServicePort orderReportServicePort;
    private final IOrderReportMapper orderReportMapper;

    @Override
    public PagedResponseDto<OrderEfficiencyResponseDto> getOrderEfficiency(
            Long restaurantId,
            Long ownerId,
            Integer page,
            Integer size
    ) {
        var orderEfficiencyResponse = orderReportServicePort.getOrderEfficiency(restaurantId, ownerId, page, size)
                .mapTo(orderReportMapper::toResponseOrder);

        return PagedResponseDto.from(orderEfficiencyResponse);
    }

    @Override
    public PagedResponseDto<EmployeeRankingResponseDto> getEmployeeRanking(
            Long restaurantId,
            Long ownerId,
            Integer page,
            Integer size
    ) {
        var employeeEfficiencyResponse = orderReportServicePort.getEmployeeRanking(restaurantId, ownerId, page, size)
                .mapTo(orderReportMapper::toResponseEmployee);

        return PagedResponseDto.from(employeeEfficiencyResponse);
    }
}
