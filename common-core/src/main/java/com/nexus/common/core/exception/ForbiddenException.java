package com.nexus.common.core.exception;

public class ForbiddenException extends DomainException {
    public ForbiddenException(String errorCode, String message) {
        super(errorCode, message);
    }
}
