package org.apache.coyote.http11.constant;

import java.util.Arrays;

public enum HeaderKey {

    HOST("Host"),
    ACCEPT("Accept"),
    CONNECTION("Connection"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    ;

    private final String value;

    HeaderKey(String value) {
        this.value = value;
    }

    public static HeaderKey find(String rawKey) {
        return Arrays.stream(values())
                .filter(value -> value.value.equals(rawKey))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 헤더입니다."));
    }

    public String getValue() {
        return this.value;
    }
}
