package org.apache.coyote.http11.util;

public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    public final int code;
    public final String reason;

    HttpStatus(int code, String reason) {
        this.code=code;
        this.reason=reason;
    }
}
