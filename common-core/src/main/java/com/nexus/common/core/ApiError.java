package com.nexus.common.core;

import java.util.List;

public class ApiError {
    private final String code;
    private final String message;
    private final List<FieldError> fieldErrors;

    public ApiError(String code, String message, List<FieldError> fieldErrors) {
        this.code = code;
        this.message = message;
        this.fieldErrors = fieldErrors == null ? List.of() : fieldErrors;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public List<FieldError> getFieldErrors() { return fieldErrors; }
}
