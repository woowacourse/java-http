package org.apache.coyote.http11.request.model;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public static HttpVersion of(final String value) {
        return Arrays.stream(HttpVersion.values())
                .findFirst()
                .filter(it -> it.value.equals(value))
                .orElseThrow();
    }

    public String getValue() {
        return value;
    }
}
