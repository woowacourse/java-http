package org.apache.coyote.domain.request.requestline;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_09("HTTP/0.9"),
    HTTP_10("HTTP/1.0"),
    HTTP_11("HTTP/1.1"),
    HTTP_20("HTTP/2.0");

    private final String message;

    HttpVersion(final String message) {
        this.message = message;
    }

    public static HttpVersion from(String message) {
        return Arrays.stream(HttpVersion.values())
                .filter(httpVersion -> message.equals(httpVersion.message))
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException("올바르지 않는 HTTP Version입니다."));
    }
}
