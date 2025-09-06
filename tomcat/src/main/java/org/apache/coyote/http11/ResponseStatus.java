package org.apache.coyote.http11;

public enum ResponseStatus {

    OK(200, "OK"),
    NOT_FOUND(404, "NOT_FOUND");

    private final int statusCode;
    private final String statusMessage;

    ResponseStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getResponseHeader() {
        return statusCode + " " + statusMessage;
    }
}
