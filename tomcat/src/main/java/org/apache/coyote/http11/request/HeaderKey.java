package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HeaderKey {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie");

    private final String key;

    HeaderKey(final String key) {
        this.key = key;
    }

    public static HeaderKey from(final String key) {
        return Arrays.stream(HeaderKey.values())
                .filter(header -> header.key.equals(key))
                .findAny()
                .orElseThrow();
    }

    public static boolean contains(final String key) {
        return Arrays.stream(HeaderKey.values())
                .anyMatch(header -> header.key.equals(key));
    }
}
