package org.apache.coyote.http11.header;

public enum RequestHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    ;

    private final String value;

    RequestHeader(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
