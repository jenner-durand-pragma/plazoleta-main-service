package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.EmployeeEfficiency;
import com.pragma.plazoleta.domain.model.OrderEfficiency;

public interface IOrderReportQueryPort {

    PagedResult<OrderEfficiency> getOrderEfficiency(Long restaurantId, Integer page, Integer size);
    PagedResult<EmployeeEfficiency> getEmployeeRanking(Long restaurantId, Integer page, Integer size);
}
