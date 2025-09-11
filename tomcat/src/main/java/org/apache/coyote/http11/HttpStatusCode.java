package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    ;

    private final int value;

    HttpStatusCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
