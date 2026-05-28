package com.pragma.plazoleta.domain.model;

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

}
