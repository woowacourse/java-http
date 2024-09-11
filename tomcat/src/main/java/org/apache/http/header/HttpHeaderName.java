package org.apache.http.header;

import java.util.Arrays;

public enum HttpHeaderName {
    ACCEPT("Accept"),
    ACCEPT_RANGES("Accept-Ranges"),
    AUTHORIZATION("Authorization"),
    CONNECTION("Connection"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    HOST("Host"),
    LOCATION("Location"),
    ORIGIN("Origin"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String value;

    HttpHeaderName(String value) {
        this.value = value;
    }

    public static HttpHeaderName from(String value) {
        return Arrays.stream(values())
                .filter(httpHeaderName -> httpHeaderName.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 HttpHeaderName이 없습니다."));
    }

    public boolean equalsIgnoreCase(String value) {
        return this.value.equalsIgnoreCase(value);
    }

    public String getValue() {
        return value;
    }
}
