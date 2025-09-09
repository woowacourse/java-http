package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "FOUND"),
    BAD_REQUEST(401, "BAD_REQUEST"),
    NOT_FOUND(404, "NOT_FOUND"),
    ;

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
