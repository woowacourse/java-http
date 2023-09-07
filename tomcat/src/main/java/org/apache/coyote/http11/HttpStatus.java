package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    BAD_REQUEST(404),
    ;

    private final int statusCode;

    HttpStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return statusCode;
    }
}
