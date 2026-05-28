package com.pragma.plazoleta.domain.model;

import lombok.*;

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

}
