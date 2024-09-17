package org.apache.coyote.http.response.line;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed");

    private final int code;
    private final String statusMessage;

    HttpStatus(int code, String statusMessage) {
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
