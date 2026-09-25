package com.nexus.common.core.exception;

public class NotFoundException extends DomainException {
    public NotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
