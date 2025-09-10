package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HttpProtocolVersion {
    HTTP1_1("HTTP/1.1"),
    ;

    private final String message;

    HttpProtocolVersion(String message) {
        this.message = message;
    }

    public static HttpProtocolVersion from(String text) {
        return Arrays.stream(values())
            .filter(version -> version.message.equals(text))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 프로토콜 버전입니다."));
    }
}
