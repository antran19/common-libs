package com.nexus.common.core.exception;

public class ConflictException extends DomainException {
    public ConflictException(String errorCode, String message) {
        super(errorCode, message);
    }
}
