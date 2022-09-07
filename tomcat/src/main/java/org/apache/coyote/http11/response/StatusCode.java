package org.apache.coyote.http11.response;

public enum StatusCode {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    NOT_FOUND(404);

    private final int value;

    StatusCode(final int value) {
        this.value = value;
    }

    public String getCode() {
        return value + " " + this.name().replace('_', ' ');
    }
}
