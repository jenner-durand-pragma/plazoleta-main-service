package com.pragma.plazoleta.infrastructure.exceptionhandler.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldErrorDetail {

    @Schema(description = "Name of the field that failed validation", example = "documentNumber")
    private String field;

    @Schema(description = "Specific reason why the validation failed", example = "Document number must be numeric only")
    private String message;

}
