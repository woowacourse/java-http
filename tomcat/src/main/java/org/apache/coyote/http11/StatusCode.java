package org.apache.coyote.http11;

public enum StatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    ;

    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String format() {
        return String.format("%d %s", code, message);
    }
}
