package com.pragma.plazoleta.infrastructure.out.feign.dto;

import com.pragma.plazoleta.domain.common.PagedResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedFeignResponseDto<T> {

    private List<T> items;

    private Integer page;

    private Integer size;

    private Long totalElements;

    private Integer totalPages;

    public PagedResult<T> toPageResult() {
        return PagedResult.of(
                items,
                page,
                size,
                totalElements,
                totalPages
        );
    }
}
