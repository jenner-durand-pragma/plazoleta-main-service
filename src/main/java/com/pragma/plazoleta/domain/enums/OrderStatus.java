package com.pragma.plazoleta.domain.enums;

import java.util.Set;

public enum OrderStatus {

    PENDING,
    IN_PREPARATION,
    READY,
    DELIVERED,
    CANCELLED;

    public static final Set<OrderStatus> ACTIVE_STATUSES = Set.of(PENDING, IN_PREPARATION, READY);

    public boolean isActive() {
        return ACTIVE_STATUSES.contains(this);
    }
}
