package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.orderreport.EmployeeRankingResponseDto;
import com.pragma.plazoleta.application.dto.response.orderreport.OrderEfficiencyResponseDto;
import com.pragma.plazoleta.application.dto.response.orderstate.OrderUserResponseDto;
import com.pragma.plazoleta.domain.model.EmployeeEfficiency;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.domain.model.UserInformation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IOrderReportMapper {
    OrderEfficiencyResponseDto toResponseOrder(OrderEfficiency model);
    EmployeeRankingResponseDto toResponseEmployee(EmployeeEfficiency model);

    OrderUserResponseDto toUserInformationResponse(UserInformation user);
}
