package com.pragma.plazoleta.domain.exception.common;

import static com.pragma.plazoleta.domain.constants.DomainConstants.MAX_PAGE_SIZE;

public class InvalidPaginationException extends RuntimeException {

    private static final String INVALID_PAGE = "Page must be greater than or equal to 0";
    private static final String INVALID_SIZE = "Size must be between 1 and " + MAX_PAGE_SIZE;

    public InvalidPaginationException(String message) {
        super(message);
    }

    public static InvalidPaginationException invalidPage() {
        return new InvalidPaginationException(INVALID_PAGE);
    }

    public static InvalidPaginationException invalidSize() {
        return new InvalidPaginationException(INVALID_SIZE);
    }
}
