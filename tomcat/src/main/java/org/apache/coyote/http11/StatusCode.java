package org.apache.coyote.http11;

public enum StatusCode {

    // 2XX
    OK(200, "OK"),

    // 3XX
    FOUND(302, "Found"),

    // 4XX
    NOT_FOUND(404, "Not Found");

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
