package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpHeader {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    HOST("Host"),
    ACCEPT("Accept"),
    CONNECTION("Connection"),
    LOCATION("Location"),
    NONE("");

    private final String value;

    HttpHeader(final String value) {
        this.value = value;
    }

    public static HttpHeader of(final String header) {
        return Arrays.stream(HttpHeader.values())
                .filter(httpHeader -> httpHeader.getValue().equals(header))
                .findFirst()
                .orElseGet(HttpHeader::getNotMatchHeader);
    }

    private static HttpHeader getNotMatchHeader() {
        return NONE;
    }

    public String getValue() {
        return value;
    }
}
