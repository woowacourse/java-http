package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpHeader {

    ACCEPT("Accept"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type");

    private final String key;

    HttpHeader(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static HttpHeader of(final String value) {
        return Arrays.stream(HttpHeader.values())
                .filter(it -> it.getKey().equals(value))
                .findFirst()
                .orElse(null);
    }
}
