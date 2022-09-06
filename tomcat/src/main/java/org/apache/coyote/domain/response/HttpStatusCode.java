package org.apache.coyote.domain.response;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found");

    private final int statusCode;
    private final String statusMessage;

    HttpStatusCode(int statusCode, String statusMessage) {
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
