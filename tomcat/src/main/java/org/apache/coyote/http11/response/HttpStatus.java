package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    UNAUTHORIZED(401, "Unauthorized"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed");

    private final int code;
    private final String value;

    HttpStatus(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int code() {
        return code;
    }

    public String value() {
        return value;
    }
}
