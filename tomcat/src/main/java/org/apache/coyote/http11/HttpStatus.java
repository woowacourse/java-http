package org.apache.coyote.http11;

public enum HttpStatus {

    OK("200", "OK");

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
