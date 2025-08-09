package com.moonsystem.gestion_commerciale.exception;

import java.util.List;

import lombok.Getter;

public class InvalidOperationException extends RuntimeException {

    @Getter
    private ErrorCodes errorCode;

    @Getter
    private List<String> errors;

    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOperationException(String message, ErrorCodes cause) {
        super(message);
        this.errorCode = cause;
    }

    public InvalidOperationException(String message, Throwable cause, ErrorCodes errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public InvalidOperationException(String message, ErrorCodes errorCode, List<String> errors) {
        super(message);
        this.errorCode = errorCode;
        this.errors = errors;
    }
}
