package org.apache.coyote.http11;

public enum StatusCode {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int statusCode;
    private final String message;

    StatusCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
