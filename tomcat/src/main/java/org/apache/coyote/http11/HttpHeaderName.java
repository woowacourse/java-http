package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpHeaderName {

    LOCATION("Location"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    HOST("Host"),
    CONNECTION("Connection")
    ;

    private final String headerName;

    HttpHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public static HttpHeaderName findByHeaderName(String headerName) {
        return Arrays.stream(HttpHeaderName.values())
                .filter(name -> name.headerName.equals(headerName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 헤더입니다."));
    }

    public String getHeaderName() {
        return headerName;
    }
}
