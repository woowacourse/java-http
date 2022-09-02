package org.apache.coyote.http11.domain;

public enum HttpStatus {

    OK(200);

    private final int statusCode;

    HttpStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    public String toStatusLine() {
        return statusCode + " " + name();
    }

    public int getStatusCode() {
        return statusCode;
    }
}
