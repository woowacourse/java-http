package org.apache.coyote.http11.common;

public enum HttpHeaderName {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location");

    private final String value;

    HttpHeaderName(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
