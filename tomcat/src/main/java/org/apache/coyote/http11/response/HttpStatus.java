package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK("200", "OK"),
    FOUND("302", "Found");

    private final String statusCode;
    private final String text;

    HttpStatus(final String statusCode, final String text) {
        this.statusCode = statusCode;
        this.text = text;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getText() {
        return text;
    }
}
