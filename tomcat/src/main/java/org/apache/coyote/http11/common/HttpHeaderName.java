package org.apache.coyote.http11.common;

public enum HttpHeaderName {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length");

    private String name;

    HttpHeaderName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
