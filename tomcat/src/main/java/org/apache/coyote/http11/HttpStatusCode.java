package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found");

    private int statusCode;
    private String statusMessage;

    HttpStatusCode(final int statusCode, final String statusMessage) {
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
