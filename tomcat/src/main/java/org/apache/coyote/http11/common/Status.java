package org.apache.coyote.http11.common;

public enum Status {

    OK(200),
    FOUND(302),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    Status(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
