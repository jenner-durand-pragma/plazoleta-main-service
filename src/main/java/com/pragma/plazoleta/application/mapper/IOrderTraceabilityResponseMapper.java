package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.orderstate.OrderStateTraceabilityResponseDto;
import com.pragma.plazoleta.application.dto.response.orderstate.OrderTraceabilityResponseDto;
import com.pragma.plazoleta.application.dto.response.orderstate.OrderUserTraceabilityResponseDto;
import com.pragma.plazoleta.domain.model.OrderStateTraceability;
import com.pragma.plazoleta.domain.model.OrderTraceability;
import com.pragma.plazoleta.domain.model.OrderUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IOrderTraceabilityResponseMapper {
    OrderTraceabilityResponseDto toResponse(OrderTraceability model);

    OrderStateTraceabilityResponseDto toStateResponse(OrderStateTraceability model);
    OrderUserTraceabilityResponseDto toUserResponse(OrderUser model);
}
