package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpVersion {
    HTTP09("HTTP/0.9"),
    HTTP10("HTTP/1.0"),
    HTTP11("HTTP/1.1"),
    ;
    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion from(String data) {
        return Arrays.stream(values()).filter(value -> value.version.equals(data)).findAny()
                .orElseThrow(()-> new IllegalArgumentException("http version validation error"));
    }
}
