package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200),
    CREATED(201),
    FOUND(302),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500)
    ;

    private final int value;

    HttpStatus(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
