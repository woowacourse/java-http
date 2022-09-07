package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200),
    FOUND(302),
    INTERNAL_SERVER_ERROR(500), UNAUTHORIZED(401),
    METHOD_NOT_ALLOWED(405)
    ;

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
