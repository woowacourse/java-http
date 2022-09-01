package org.apache.coyote.common;

import java.util.Arrays;

public enum HttpVersion {

    HTTP11("HTTP/1.1"),
    HTTP10("HTTP/1.0")
    ;

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public static HttpVersion of(final String value) {
        return Arrays.stream(values())
                .filter(http -> http.value.equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String getValue() {
        return value;
    }
}
