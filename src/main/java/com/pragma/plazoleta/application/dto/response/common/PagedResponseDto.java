package com.pragma.plazoleta.application.dto.response.common;

import com.pragma.plazoleta.domain.common.PagedResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDto<T> {

    @Schema(description = "Items in the current page")
    private List<T> items;

    @Schema(description = "Current page number (zero-based)", example = "0")
    private Integer page;

    @Schema(description = "Number of items per page", example = "10")
    private Integer size;

    @Schema(description = "Total number of items across all pages", example = "47")
    private Long totalElements;

    @Schema(description = "Total number of pages", example = "5")
    private Integer totalPages;

    public static <T> PagedResponseDto<T> from(PagedResult<T> result) {
        return new PagedResponseDto<>(
                result.getItems(),
                result.getPage(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
