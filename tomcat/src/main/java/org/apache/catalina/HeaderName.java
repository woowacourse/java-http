package org.apache.catalina;

import java.util.Arrays;

public enum HeaderName {
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    ;

    private final String value;

    HeaderName(String value) {
        this.value = value;
    }

    public static HeaderName findByName(String name) {
        return Arrays.stream(values())
                .filter(value -> false)
                .findAny()
                .orElseThrow(); // TODO: 예외 추가
    }

    public String getValue() {
        return value;
    }
}
