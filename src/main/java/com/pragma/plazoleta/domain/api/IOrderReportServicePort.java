package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.EmployeeEfficiency;
import com.pragma.plazoleta.domain.model.OrderEfficiency;

public interface IOrderReportServicePort {

    PagedResult<OrderEfficiency> getOrderEfficiency(Long restaurantId, Long ownerId, Integer page, Integer size);
    PagedResult<EmployeeEfficiency> getEmployeeRanking(Long restaurantId, Long ownerId, Integer page, Integer size);
}
