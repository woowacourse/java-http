package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200);

    private int statusCode;

    HttpStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    public String toStatusLine() {
        return String.valueOf(statusCode) + " " + name();
    }

    public int getStatusCode() {
        return statusCode;
    }
}
