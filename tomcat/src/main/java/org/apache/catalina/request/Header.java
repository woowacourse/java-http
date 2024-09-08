package org.apache.catalina.request;

import java.util.Arrays;

public enum Header {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    HOST("Host"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    OTHER("Other"),
    ;

    private final String value;

    Header(String value) {
        this.value = value;
    }

    public static Header of(String value) {
        return Arrays.stream(values())
                .filter(header -> header.value.equals(value))
                .findFirst()
                .orElse(OTHER);
    }
}
