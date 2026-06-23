package com.pragma.plazoleta.infrastructure.out.feign.mapper;

import com.pragma.plazoleta.domain.model.EmployeeEfficiency;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.domain.model.OrderUser;
import com.pragma.plazoleta.infrastructure.out.feign.dto.EmployeeEfficiencyFeignResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderEfficiencyFeignResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderStateUserInformationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IOrderReportFeignMapper {
    OrderEfficiency toModelOrder(OrderEfficiencyFeignResponseDto responseDto);
    EmployeeEfficiency toModelEmployee(EmployeeEfficiencyFeignResponseDto responseDto);

    OrderUser toModelUser(OrderStateUserInformationDto userInformationDto);
}
