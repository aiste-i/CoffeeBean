package org.coffee.service.exceptions;

public class CredentialChangeException extends RuntimeException {
    public CredentialChangeException(String message) {
        super(message);
    }

    public CredentialChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
