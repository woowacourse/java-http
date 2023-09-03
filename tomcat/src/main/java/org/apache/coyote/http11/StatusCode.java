package org.apache.coyote.http11;

public enum StatusCode {

    OK(200, "OK"),
    CREATED(201, "CREATED"),
    FOUND(302, "FOUND"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED")
    ;

    private final int code;
    private final String message;

    StatusCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
