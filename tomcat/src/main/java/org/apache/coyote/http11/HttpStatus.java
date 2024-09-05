package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    CREATED(201),
    ;

    private final int statusCode;

    HttpStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getHeaderForm() {
        return this.name() + " " + statusCode;
    }
}
