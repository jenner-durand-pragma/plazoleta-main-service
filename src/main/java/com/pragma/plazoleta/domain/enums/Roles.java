package com.pragma.plazoleta.domain.enums;

import com.pragma.plazoleta.domain.constants.RoleConstants;
import lombok.Getter;

@Getter
public enum Roles {

    ADMIN(1L, RoleConstants.ROLE_ADMIN),
    OWNER(2L, RoleConstants.ROLE_OWNER),
    EMPLOYEE(3L, RoleConstants.ROLE_EMPLOYEE),
    CLIENT(4L, RoleConstants.ROLE_CLIENT);

    private final Long id;
    private final String name;

    Roles(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
