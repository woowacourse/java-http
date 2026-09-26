package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public static HttpVersion from(final String value) {
        return Arrays.stream(values())
                .filter(version -> version.value.equals(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("지원하지 않는 HTTP Version: " + value));
    }
}
