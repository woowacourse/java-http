package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found");

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
