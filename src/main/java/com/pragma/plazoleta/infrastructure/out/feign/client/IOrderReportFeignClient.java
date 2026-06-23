package com.pragma.plazoleta.infrastructure.out.feign.client;

import com.pragma.plazoleta.infrastructure.out.feign.dto.EmployeeEfficiencyFeignResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderEfficiencyFeignResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.PagedFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "traceability-service",
        url = "${traceability-service.url}",
        path = "/api/v1/traceability/restaurants"
)
public interface IOrderReportFeignClient {
    @GetMapping(
            value = "/{restaurantId}/efficiency",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    PagedFeignResponseDto<OrderEfficiencyFeignResponseDto> getOrderEfficiency(
            @PathVariable Long restaurantId,
            @RequestParam Integer page,
            @RequestParam Integer size
    );

    @GetMapping(
            value = "/{restaurantId}/employees-ranking",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    PagedFeignResponseDto<EmployeeEfficiencyFeignResponseDto> getEmployeeRanking(
            @PathVariable Long restaurantId,
            @RequestParam Integer page,
            @RequestParam Integer size
    );
}
