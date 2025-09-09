package org.apache.coyote.http11.httpResponse;

public enum HttpStatus {

    OK(200),
    FOUND(302),
    NOT_FOUND(404),
    ;

    private final int statusCode;

    HttpStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return this.statusCode + " " + name().replace("_", " ");
    }
}
