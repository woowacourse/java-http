package org.apache.http.info;

import java.util.Arrays;
import java.util.Objects;

public enum HttpHeaderName {

    METHOD("Method"),
    REQUEST_URI("RequestURI"),
    PROTOCOL("Protocol"),
    HOST("Host"),
    CONNECTION("Connection"),
    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie"),
    CACHE_CONTROL("Cache-Control"),
    CONTENT_LENGTH("Content-Length"),
    ;

    private final String headerName;

    HttpHeaderName(final String headerName) {
        this.headerName = headerName;
    }

    public static HttpHeaderName from(String httpHeader) {
        return Arrays.stream(values())
                .filter(value -> Objects.equals(value.headerName.toUpperCase(), httpHeader.toUpperCase()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String getName() {
        return headerName;
    }
}
