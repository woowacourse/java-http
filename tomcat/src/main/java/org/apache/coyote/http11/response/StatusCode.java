package org.apache.coyote.http11.response;

public enum StatusCode {
    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    StatusCode(final int code) {
        this.code = code;
    }

    public String getStatus() {
        return String.join(" ", String.valueOf(code), name());
    }
}
