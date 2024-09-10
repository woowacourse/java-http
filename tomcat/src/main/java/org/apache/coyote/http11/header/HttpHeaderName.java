package org.apache.coyote.http11.header;

import java.util.Arrays;

public enum HttpHeaderName {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie");

    private final String value;

    HttpHeaderName(String name) {
        this.value = name;
    }

    public HttpHeaderName findByName(final String name) {
        return Arrays.stream(HttpHeaderName.values())
                .filter(headerName -> headerName.value.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HTTP Header 이름입니다."));
    }

    public String getName() {
        return value;
    }
}
