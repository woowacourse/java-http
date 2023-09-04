package org.apache.coyote.http11;

public enum HttpStatus {
    OK("OK", 200),
    FOUND("Found", 302),
    UNAUTHORIZED("Unauthorized", 401);

    private final String message;
    private final int code;

    HttpStatus(final String message, final int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
