package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200, "OK"),
    CREATED(201, "CREATED"),
    FOUND(302, "FOUND"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT_FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

    private final int code;
    private final String message;

    HttpStatus(final int code, final String message) {
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
