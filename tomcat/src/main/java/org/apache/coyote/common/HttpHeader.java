package org.apache.coyote.common;

public enum HttpHeader {

    ACCEPT("Accept"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    HOST("Host"),
    CONNECTION("Connection");

    private final String source;

    HttpHeader(final String source) {
        this.source = source;
    }

    public String source() {
        return source;
    }
}
