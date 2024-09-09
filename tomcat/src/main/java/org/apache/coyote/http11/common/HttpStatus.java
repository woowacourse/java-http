package org.apache.coyote.http11.common;

public enum HttpStatus {

    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int statusCode;
    private final String statusMessage;

    HttpStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public int statusCode() {
        return statusCode;
    }

    public String statusMessage() {
        return statusMessage;
    }
}
