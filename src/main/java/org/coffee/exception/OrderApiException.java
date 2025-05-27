package org.coffee.exception;

public class OrderApiException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int httpStatusCode;

    public OrderApiException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public OrderApiException(String message, int httpStatusCode, Throwable cause) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}