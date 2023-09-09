package org.apache.coyote.http11.response;

public enum StatusCode {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    NOT_FOUND(404);

    private final int code;

    StatusCode(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return this.name().replace("_", " ");
    }
}
