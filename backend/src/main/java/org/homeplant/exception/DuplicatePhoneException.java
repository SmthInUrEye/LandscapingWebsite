package org.homeplant.exception;

public class DuplicatePhoneException extends RuntimeException {
    public DuplicatePhoneException(String message) {
        super(message);
    }

    public DuplicatePhoneException(String message, Throwable cause) {
        super(message, cause);
    }
}