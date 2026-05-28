package com.pragma.plazoleta.infrastructure.exceptionhandler;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;
import com.pragma.plazoleta.domain.exception.ConflictException;
import com.pragma.plazoleta.domain.exception.NotFoundException;
import com.pragma.plazoleta.infrastructure.exceptionhandler.common.ErrorResponse;
import com.pragma.plazoleta.infrastructure.exceptionhandler.common.FieldErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── 409 Conflict ───
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex,
            WebRequest request
    ) {
        var fieldErrors = ex.getField()
                .map(field -> List.of(new FieldErrorDetail(field, ex.getMessage())))
                .orElse(Collections.emptyList());
        return build(HttpStatus.CONFLICT, ex.getMessage(), request, fieldErrors);
    }

    // ─── 404 Not Found ───
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex,
            WebRequest request
    ) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, Collections.emptyList());
    }

    // ─── 422 Unprocessable Entity ───
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException ex,
            WebRequest request
    ) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request, Collections.emptyList());
    }

    // ─── 400 Bad Request (RequestBody) ───
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBodyValidation(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        var fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldDetail)
                .collect(Collectors.toList());

        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors);
    }

    // ─── 400 Bad Request (RequestParam, PathVariable) ───
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request
    ) {
        List<FieldErrorDetail> fieldErrors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            var field = violation.getPropertyPath() == null
                    ? ""
                    : violation.getPropertyPath().toString();
            fieldErrors.add(new FieldErrorDetail(field, violation.getMessage()));
        }

        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors);
    }

    // ─── 400 Bad Request (Malformed) ───
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Malformed or unreadable request body",
                request,
                Collections.emptyList()
        );
    }

    // ─── 400 Bad Request (Missing required request parameter) ───
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex,
            WebRequest request
    ) {
        var message = String.format("Required parameter '%s' is missing", ex.getParameterName());

        return build(
                HttpStatus.BAD_REQUEST,
                message,
                request,
                List.of(new FieldErrorDetail(ex.getParameterName(), message)));
    }

    // ─── 400 Bad Request (Type Mismatch) ───
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request
    ) {
        var required = Optional.ofNullable(ex.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("expected type");
        var message = String.format("Parameter '%s' should be of type %s", ex.getName(), required);

        return build(
                HttpStatus.BAD_REQUEST,
                message,
                request,
                List.of(new FieldErrorDetail(ex.getName(), message))
        );
    }

    // ─── 405 HTTP Method not supported ───
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            WebRequest request
    ) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request, Collections.emptyList());
    }

    // ─── 500 Internal Server Error ───
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            WebRequest request
    ) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request,
                Collections.emptyList()
        );
    }

    // ─── Utilities ───

    private FieldErrorDetail toFieldDetail(FieldError fieldError) {
        return new FieldErrorDetail(
                fieldError.getField(),
                fieldError.getDefaultMessage()
        );
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String message,
            WebRequest request,
            List<FieldErrorDetail> fieldErrors
    ) {
        var body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                extractPath(request),
                fieldErrors
        );

        return new ResponseEntity<>(body, status);
    }

    private String extractPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }

        return "";
    }
}