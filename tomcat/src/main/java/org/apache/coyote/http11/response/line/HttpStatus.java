package org.apache.coyote.http11.response.line;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized");


    private final int code;
    private final String statusMessage;

    HttpStatus(final int code, final String statusMessage) {
        this.code = code;
        this.statusMessage = statusMessage;
    }

    public int getCode() {
        return code;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
