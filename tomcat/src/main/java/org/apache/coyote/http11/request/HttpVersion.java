package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpVersion {
    HTTP11("HTTP/1.1");

    private final String name;

    HttpVersion(String name) {
        this.name = name;
    }

    public static HttpVersion of(String name) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.name.equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String getName() {
        return name;
    }
}
