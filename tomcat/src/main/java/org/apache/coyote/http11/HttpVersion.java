package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion from(String httpVersion) {
        return Arrays.stream(values())
                .filter(value -> value.version.equals(httpVersion))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Not found HTTP Version : %s", httpVersion)));
    }

    @Override
    public String toString() {
        return version;
    }
}
