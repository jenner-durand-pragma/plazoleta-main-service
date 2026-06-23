package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.model.EmployeeEfficiency;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.domain.spi.IOrderReportQueryPort;
import com.pragma.plazoleta.infrastructure.out.feign.client.IOrderReportFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.mapper.IOrderReportFeignMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderReportAdapter implements IOrderReportQueryPort {

    private final IOrderReportFeignClient orderReportFeignClient;
    private final IOrderReportFeignMapper orderReportFeignMapper;

    @Override
    public PagedResult<OrderEfficiency> getOrderEfficiency(Long restaurantId, Integer page, Integer size) {
        return orderReportFeignClient.getOrderEfficiency(restaurantId, page, size)
                .toPageResult()
                .mapTo(orderReportFeignMapper::toModelOrder);
    }

    @Override
    public PagedResult<EmployeeEfficiency> getEmployeeRanking(Long restaurantId, Integer page, Integer size) {
        return orderReportFeignClient.getEmployeeRanking(restaurantId, page, size)
                .toPageResult()
                .mapTo(orderReportFeignMapper::toModelEmployee);
    }
}
