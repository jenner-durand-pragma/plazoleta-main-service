package com.pragma.plazoleta.domain.common;

import com.pragma.plazoleta.domain.constants.DomainConstants;
import com.pragma.plazoleta.domain.exception.common.InvalidPaginationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedResult<T> {

    private List<T> items;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;

    public static <T> PagedResult<T> of(
            List<T> items,
            Integer page,
            Integer size,
            Long totalElements,
            Integer totalPages
    ) {
        return new PagedResult<>(items, page, size, totalElements, totalPages);
    }

    public <R> PagedResult<R> mapTo(Function<? super T, R> mapper) {
        var itemsMapped = items.stream().map(mapper).collect(Collectors.toUnmodifiableList());

        return new PagedResult<>(itemsMapped, page, size, totalElements, totalPages);
    }

    public static void validatePagination(Integer page, Integer size) {
        if (page < 0) {
            throw InvalidPaginationException.invalidPage();
        }

        if (size <= 0 || size > DomainConstants.MAX_PAGE_SIZE) {
            throw InvalidPaginationException.invalidSize();
        }
    }
}
