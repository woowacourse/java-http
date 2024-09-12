package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpHeader {
    LOCATION("Location"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    HOST("Host"),
    CONNECTION("Connection"),
    ACCEPT("Accept")
    ;

    private final String name;

    HttpHeader(String name) {
        this.name = name;
    }

    public static HttpHeader findByName(String name) {
        return Arrays.stream(HttpHeader.values())
                .filter(header -> header.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("입력 값과 일치하는 HttpHeader가 없습니다. 입력 = " + name));
    }

    public String getName() {
        return name;
    }
}
