package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200),
    FOUND(302),
    ;

    private final int statusCode;

    HttpStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return this.name();
    }

    public int getStatusCode() {
        return statusCode;
    }
}
