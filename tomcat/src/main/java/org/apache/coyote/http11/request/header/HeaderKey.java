package org.apache.coyote.http11.request.header;

import java.util.Arrays;

public enum HeaderKey {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie");

    private static final String KEY_VALUE_DELIMITER = ": ";
    private static final int VALUE_INDEX = 1;

    private final String key;

    HeaderKey(final String key) {
        this.key = key;
    }

    public static HeaderKey from(final String line) {
        return Arrays.stream(HeaderKey.values())
                .filter(header -> line.contains(header.key))
                .findAny()
                .orElseThrow();
    }

    public static boolean existsInLine(final String line) {
        return Arrays.stream(HeaderKey.values())
                .anyMatch(header -> line.contains(header.key));
    }

    public static String removeKeyFromLine(final String line) {
        return line.split(KEY_VALUE_DELIMITER)[VALUE_INDEX];
    }

    public String toHeader(final String value) {
        return key + KEY_VALUE_DELIMITER + value;
    }
}
