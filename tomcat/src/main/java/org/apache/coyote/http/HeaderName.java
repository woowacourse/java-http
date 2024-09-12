package org.apache.coyote.http;

import java.util.Arrays;

public enum HeaderName {
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),

    ;

    private final String value;

    HeaderName(String value) {
        this.value = value;
    }

    public static HeaderName findByName(String name) {
        return Arrays.stream(values())
                .filter(headerName -> headerName.value.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("HeaderName not found"));
    }

    public String getValue() {
        return value;
    }
}
