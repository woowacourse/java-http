package org.apache.coyote.http;

public enum HttpHeader {

    CONTENT_LENGTH("Content-Length");

    private final String value;

    HttpHeader(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
