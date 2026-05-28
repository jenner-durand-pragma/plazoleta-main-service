package com.pragma.plazoleta.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Roles {

    ADMIN(1L, "ADMIN"),
    OWNER(2L, "OWNER"),
    EMPLOYEE(3L, "EMPLOYEE"),
    CLIENT(4L, "CLIENT");

    private final Long id;
    private final String name;

}
