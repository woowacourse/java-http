package org.apache.coyote.http11.enums;

public enum HttpStatus {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    ;

    private final int value;

    HttpStatus(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
