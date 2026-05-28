package com.pragma.plazoleta.domain.model;

import com.pragma.plazoleta.domain.exception.dish.DishOwnershipException;
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
public class Dish {

    private Long id;
    private String name;
    private String description;
    private Integer price;
    private String imageUrl;
    private Boolean active;

    private Category category;
    private Restaurant restaurant;

    public void checkOwnership(Long ownerId) {
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new DishOwnershipException();
        }
    }
}
