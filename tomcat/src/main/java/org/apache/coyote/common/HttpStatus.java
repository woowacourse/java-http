package org.apache.coyote.common;

public enum HttpStatus {
    OK(200),
    NOT_FOUND(404),
    FOUND(302),
    ;

    private final int statusCode;

    HttpStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
