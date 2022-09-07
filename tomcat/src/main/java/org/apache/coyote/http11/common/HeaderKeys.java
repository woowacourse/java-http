package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HeaderKeys {

    HOST("Host"),
    CONNECTION("Connection"),

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    ;

    private final String name;

    HeaderKeys(final String name) {
        this.name = name;
    }

    public static HeaderKeys from(final String headerName) {
        return Arrays.stream(values())
            .filter(value -> value.name.equals(headerName))
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException(String.format("정의되지 않은 Header key 입니다. [%s]", headerName)));
    }

    public String getName() {
        return name;
    }
}
