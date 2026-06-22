package com.pragma.plazoleta.infrastructure.out.feign.mapper;

import com.pragma.plazoleta.domain.model.OrderStateTraceability;
import com.pragma.plazoleta.domain.model.OrderTraceability;
import com.pragma.plazoleta.domain.model.OrderUserTraceability;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderStateTraceabilityResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderStateUserInformationDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderTraceabilityResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IOrderTraceabilityFeignMapper {
    OrderTraceability toModel(OrderTraceabilityResponseDto response);

    OrderUserTraceability toOrderUserModel(OrderStateUserInformationDto userInformationDto);
    OrderStateTraceability toStateModel(OrderStateTraceabilityResponseDto stateResponseDto);
}
