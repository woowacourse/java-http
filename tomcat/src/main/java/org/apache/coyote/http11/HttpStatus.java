package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    SERVER_ERROR(500, "Server Error");

    private final int statusCode;
    private final String message;

    HttpStatus(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
