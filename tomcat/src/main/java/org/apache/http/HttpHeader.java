package org.apache.http;

import java.util.Arrays;
import java.util.Optional;

public enum HttpHeader {

    ACCEPT("Accept"),
    CONNECTION("Connection"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    HOST("Host"),
    ;

    private final String value;

    HttpHeader(final String value) {
        this.value = value;
    }

    public static Optional<HttpHeader> find(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.value.equals(value))
                .findFirst();
    }

    public String getValue() {
        return value;
    }
}
