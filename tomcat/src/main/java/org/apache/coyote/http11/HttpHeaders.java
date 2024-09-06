package org.apache.coyote.http11;

public enum HttpHeaders {

    HOST("Host"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location");

    private final String header;

    HttpHeaders(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
