package org.apache.coyote.http11;

public enum HttpStatus {

    OK("200 OK"),
    FOUND("302 Found"),
    NOT_FOUND("404 Not Found"),
    METHOD_NOT_ALLOWED("405 Method Not Allowed");

    private final String statusLine;

    HttpStatus(final String statusLine) {
        this.statusLine = statusLine;
    }

    public String getStatusLine() {
        return statusLine;
    }
}
