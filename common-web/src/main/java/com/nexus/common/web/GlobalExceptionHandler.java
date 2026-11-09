package com.nexus.common.web;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.nexus.common.core.ApiError;
import com.nexus.common.core.ApiResponse;
import com.nexus.common.core.FieldError;
import com.nexus.common.core.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ClassUtils;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(NotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getErrorCode(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(ConflictException ex) {
        return build(HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(UnauthorizedException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getErrorCode(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbidden(ForbiddenException ex) {
        return build(HttpStatus.FORBIDDEN, ex.getErrorCode(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(ValidationException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage(), ex.getFieldErrors());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleBeanValidation(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "One or more fields are invalid", fieldErrors);
    }

    // A body that cannot be read is the caller's mistake, never a server error. When Jackson says which
    // field it choked on (a text where a number is expected, a date that is not a date, an unknown enum
    // value) the answer names that field like any other validation error; when there is no field (broken
    // JSON, an empty body) it is MALFORMED_REQUEST. Jackson's own message is not echoed: it quotes parser
    // internals and class names.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof MismatchedInputException mismatch && !mismatch.getPath().isEmpty()) {
            String message = mismatch instanceof InvalidFormatException invalid
                    ? describeExpected(invalid.getTargetType())
                    : "has an invalid type";
            return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "One or more fields are invalid",
                    List.of(new FieldError(fieldPath(mismatch), message)));
        }
        return build(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Request body is missing or is not valid JSON",
                List.of());
    }

    // A path or query value of the wrong type (page=abc, size=1.5).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "One or more fields are invalid",
                List.of(new FieldError(ex.getName(), describeExpected(ex.getRequiredType()))));
    }

    // items[0].quantity: field names joined by dots, list positions in brackets.
    private static String fieldPath(JsonMappingException ex) {
        StringBuilder path = new StringBuilder();
        for (JsonMappingException.Reference ref : ex.getPath()) {
            if (ref.getFieldName() != null) {
                if (!path.isEmpty()) {
                    path.append('.');
                }
                path.append(ref.getFieldName());
            } else if (ref.getIndex() >= 0) {
                path.append('[').append(ref.getIndex()).append(']');
            }
        }
        return path.toString();
    }

    private static String describeExpected(Class<?> type) {
        if (type == null) {
            return "has an invalid value";
        }
        Class<?> boxed = ClassUtils.resolvePrimitiveIfNecessary(type);
        if (Number.class.isAssignableFrom(boxed)) {
            return "must be a number";
        }
        if (Boolean.class.equals(boxed)) {
            return "must be true or false";
        }
        if (boxed.isEnum()) {
            return "must be one of " + Arrays.toString(boxed.getEnumConstants());
        }
        if (TemporalAccessor.class.isAssignableFrom(boxed)) {
            return "must be a valid ISO-8601 date or time";
        }
        if (UUID.class.equals(boxed)) {
            return "must be a valid UUID";
        }
        return "has an invalid value";
    }

    // org.springframework.web.ErrorResponse is an interface, not a Throwable, so it cannot be used
    // directly as an @ExceptionHandler value (Spring requires Class<? extends Throwable>). Instead we
    // enumerate the concrete built-in Spring MVC exceptions that implement it, which covers every
    // "wrong method / wrong media type / unmapped route / etc." case Spring itself would otherwise
    // resolve to a real 4xx. MethodArgumentNotValidException also implements ErrorResponse but is
    // deliberately left off this list: it keeps its own more specific handler above.
    // NOTE: HttpMessageNotReadableException (unreadable JSON body) and MethodArgumentTypeMismatchException do NOT
    // implement ErrorResponse in Spring Framework 6.1.x, so they have their own handlers above.
    @ExceptionHandler({
            HttpMediaTypeException.class,
            HttpRequestMethodNotSupportedException.class,
            ServletRequestBindingException.class,
            MissingServletRequestPartException.class,
            NoHandlerFoundException.class,
            NoResourceFoundException.class,
            AsyncRequestTimeoutException.class,
            MaxUploadSizeExceededException.class,
            ErrorResponseException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleErrorResponse(org.springframework.web.ErrorResponse ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getBody().getDetail() != null ? ex.getBody().getDetail() : status.getReasonPhrase();
        return build(status, "REQUEST_ERROR", message, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error", List.of());
    }

    private ResponseEntity<ApiResponse<Object>> build(HttpStatus status, String code, String message, List<FieldError> fieldErrors) {
        ApiError error = new ApiError(code, message, fieldErrors);
        return ResponseEntity.status(status).body(ApiResponse.error(error));
    }
}
