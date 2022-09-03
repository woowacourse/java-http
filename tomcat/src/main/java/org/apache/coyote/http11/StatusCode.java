package org.apache.coyote.http11;

public enum StatusCode {

    OK(200, "OK");

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
