package org.apache.coyote.common;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String source;

    HttpVersion(final String source) {
        this.source = source;
    }

    public static HttpVersion from(final String source) {
        return Arrays.stream(HttpVersion.values())
                .filter(httpVersion -> httpVersion.source.equals(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException());
    }

    public String version() {
        return source;
    }

    @Override
    public String toString() {
        return "HttpVersion{" +
               "source='" + source + '\'' +
               '}';
    }
}
