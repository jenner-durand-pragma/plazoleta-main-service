package com.pragma.plazoleta.domain.model;

import com.pragma.plazoleta.domain.exception.restaurant.RestaurantOwnershipException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    private Long id;
    private String name;
    private String address;
    private Long ownerId;
    private String phone;
    private String logoUrl;
    private String nit;

    public void checkOwnership(Long ownerId) {
        if (!this.ownerId.equals(ownerId)) {

            throw new RestaurantOwnershipException();
        }
    }
}
