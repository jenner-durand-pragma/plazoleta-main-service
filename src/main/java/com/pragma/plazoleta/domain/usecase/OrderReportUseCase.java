package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IOrderReportServicePort;
import com.pragma.plazoleta.domain.common.PagedResult;
import com.pragma.plazoleta.domain.exception.restaurant.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.EmployeeEfficiency;
import com.pragma.plazoleta.domain.model.OrderEfficiency;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IOrderReportQueryPort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderReportUseCase implements IOrderReportServicePort {

    private final IOrderReportQueryPort orderReportQueryPort;
    private final IRestaurantPersistencePort restaurantPersistencePort;

    @Override
    public PagedResult<OrderEfficiency> getOrderEfficiency(Long restaurantId, Long ownerId, Integer page, Integer size) {
        var restaurant = resolveRestaurant(restaurantId);
        restaurant.checkOwnership(ownerId);

        return orderReportQueryPort.getOrderEfficiency(restaurantId, page, size);
    }

    @Override
    public PagedResult<EmployeeEfficiency> getEmployeeRanking(Long restaurantId, Long ownerId, Integer page, Integer size) {
        var restaurant = resolveRestaurant(restaurantId);
        restaurant.checkOwnership(ownerId);

        return orderReportQueryPort.getEmployeeRanking(restaurantId, page, size);
    }

    private Restaurant resolveRestaurant(Long restaurantId) {
        var restaurant = restaurantPersistencePort.findById(restaurantId);
        if (restaurant == null) {
            throw new RestaurantNotFoundException(restaurantId);
        }

        return restaurant;
    }
}
