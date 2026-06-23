package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.orderreport.EmployeeRankingResponseDto;
import com.pragma.plazoleta.application.dto.response.orderreport.OrderEfficiencyResponseDto;

public interface IOrderReportHandler {

    PagedResponseDto<OrderEfficiencyResponseDto> getOrderEfficiency(
            Long restaurantId,
            Long ownerId,
            Integer page,
            Integer size
    );
    PagedResponseDto<EmployeeRankingResponseDto> getEmployeeRanking(
            Long restaurantId,
            Long ownerId,
            Integer page,
            Integer size
    );
}
