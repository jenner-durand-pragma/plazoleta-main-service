package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.response.common.PagedResponseDto;
import com.pragma.plazoleta.application.dto.response.orderreport.EmployeeRankingResponseDto;
import com.pragma.plazoleta.application.dto.response.orderreport.OrderEfficiencyResponseDto;
import com.pragma.plazoleta.application.handler.IOrderReportHandler;
import com.pragma.plazoleta.infrastructure.configuration.security.annotation.IsOwner;
import com.pragma.plazoleta.infrastructure.configuration.security.token.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}")
@RequiredArgsConstructor
@Tag(name = "Orders Reports", description = "Owner orders reports on restaurant efficiency")
public class OrderReportRestController {

    private final IOrderReportHandler orderReportHandler;

    @IsOwner
    @GetMapping("/efficiency")
    public ResponseEntity<PagedResponseDto<OrderEfficiencyResponseDto>> getOrderEfficiency(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return null;
    }

    @IsOwner
    @GetMapping("/employees-ranking")
    public ResponseEntity<PagedResponseDto<EmployeeRankingResponseDto>> getEmployeeRanking(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return null;
    }
}
