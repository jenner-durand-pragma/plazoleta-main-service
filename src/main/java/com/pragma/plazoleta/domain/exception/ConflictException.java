package com.pragma.plazoleta.domain.exception;

import java.util.Optional;

public class ConflictException extends RuntimeException {
    private final String field;

    protected ConflictException(String message, String field) {
        super(message);
        this.field = field;
    }

    protected ConflictException(String message) {
        super(message);
        this.field = null;
    }

    public Optional<String> getField() {
        return Optional.ofNullable(field);
    }
}
