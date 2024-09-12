package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    ;

    private final int statusCode;
    private final String statusMessage;

    HttpStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
