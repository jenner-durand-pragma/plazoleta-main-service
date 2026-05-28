package com.pragma.plazoleta.domain.exception;

public class BusinessRuleException extends RuntimeException {

    protected BusinessRuleException(String message) {
        super(message);
    }
}
