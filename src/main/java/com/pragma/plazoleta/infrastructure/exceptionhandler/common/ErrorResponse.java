package com.pragma.plazoleta.infrastructure.exceptionhandler.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2026-05-28T10:15:30")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private Integer status;

    @Schema(description = "HTTP error type", example = "Bad Request")
    private String error;

    @Schema(description = "General error message explaining the issue", example = "Validation failed")
    private String message;

    @Schema(description = "URI path where the error was triggered", example = "/api/v1/main")
    private String path;

    @Schema(description = "List of specific field validation errors (empty if not applicable)")
    private List<FieldErrorDetail> fieldErrors;

}
