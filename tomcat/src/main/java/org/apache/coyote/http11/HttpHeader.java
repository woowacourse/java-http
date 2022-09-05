package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpHeader {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    HOST("Host"),
    CONNECTION("Connection");

    private final String value;

    HttpHeader(final String value) {
        this.value = value;
    }

    public static HttpHeader of(final String value) {
        return Arrays.stream(HttpHeader.values())
                .filter(httpHeader -> httpHeader.value.equals(value))
                .findAny()
                .orElseThrow();
    }

    public String getValue() {
        return value;
    }
}
