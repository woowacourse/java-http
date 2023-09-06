package org.apache.coyote.http;

import java.util.Arrays;
import java.util.Optional;

public enum HttpHeader {

    ACCEPT("Accept"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie");

    private final String key;

    HttpHeader(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static Optional<HttpHeader> from(final String value) {
        return Arrays.stream(HttpHeader.values())
                .filter(it -> it.getKey().equals(value))
                .findFirst();
    }
}
