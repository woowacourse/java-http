package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpVersion {

    HTTP11("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static HttpVersion findByValue(String value) {
        return Arrays.stream(HttpVersion.values())
                .filter(httpVersion -> httpVersion.value.equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Not Supported Http Version"));
    }
}
