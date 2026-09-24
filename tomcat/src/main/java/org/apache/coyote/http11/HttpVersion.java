package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Objects;

public enum HttpVersion {
    VERSION_11("HTTP/1.1");

    private final String name;

    HttpVersion(final String name) {
        this.name = name;
    }

    public static HttpVersion pick(String name) {
        return Arrays.stream(values())
            .filter(httpVersion -> Objects.equals(httpVersion.name, name))
            .findFirst()
            .orElseThrow();
    }
}
