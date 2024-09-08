package org.apache.coyote.http;

public enum StatusCode {
    _200(200, "OK"),
    _302(302, "FOUND"),
    _401(401, "UNAUTHORIZED"),
    _404(404, "NOT FOUND"),
    ;

    private final int code;
    private final String message;

    StatusCode(int code, String message) {
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
