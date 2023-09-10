package org.apache.coyote.request;

import org.apache.coyote.Header;

public enum RequestHeader implements Header {
    CONTENT_LENGTH("Content-Length"),
    ACCEPT("Accept"),
    COOKIE("Cookie");

    private final String name;

    RequestHeader(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
