package com.pragma.plazoleta.infrastructure.out.feign.client;

import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderStateRequestDto;
import com.pragma.plazoleta.infrastructure.out.feign.dto.OrderTraceabilityResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "traceability-service", url = "${traceability-service.url}", path = "/api/v1/traceability/orders")
public interface IOrderTraceabilityFeignClient {

    @PostMapping(value = "/states", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveState(@RequestBody OrderStateRequestDto request);

    @GetMapping(value = "/{orderId}")
    OrderTraceabilityResponseDto findByOrderId(@PathVariable("orderId") Long orderId);
}
