package org.apache.coyote.common;

import java.util.Arrays;

import static java.util.Locale.ENGLISH;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String version;

    HttpVersion(final String version) {
        this.version = version;
    }

    public static HttpVersion find(final String source) {
        return Arrays.stream(HttpVersion.values())
                .filter(httpVersion -> httpVersion.contains(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException());
    }

    private boolean contains(final String source) {
        return source.toUpperCase(ENGLISH)
                .contains(this.version);
    }

    public String version() {
        return version;
    }

    @Override
    public String toString() {
        return version();
    }
}
