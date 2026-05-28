package com.pragma.plazoleta.domain.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String resource;
    private final String resourceId;

    protected NotFoundException(String message, String resource, Object resourceId) {
        super(message);
        this.resource = resource;
        this.resourceId = String.valueOf(resourceId);
    }
}
