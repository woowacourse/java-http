package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200),
    NOT_MODIFIED(304);

    private final int statusCode;

    HttpStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
