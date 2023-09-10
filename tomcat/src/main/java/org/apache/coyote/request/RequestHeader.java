package org.apache.coyote.request;

public enum RequestHeader {
    CONTENT_LENGTH("Content-Length"),
    ACCEPT("Accept");

    private final String name;

    RequestHeader(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
