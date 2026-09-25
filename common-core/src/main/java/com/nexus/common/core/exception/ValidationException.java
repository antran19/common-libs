package com.nexus.common.core.exception;

import com.nexus.common.core.FieldError;
import java.util.List;

public class ValidationException extends DomainException {
    private final List<FieldError> fieldErrors;

    public ValidationException(List<FieldError> fieldErrors) {
        super("VALIDATION_ERROR", "One or more fields are invalid");
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() { return fieldErrors; }
}
