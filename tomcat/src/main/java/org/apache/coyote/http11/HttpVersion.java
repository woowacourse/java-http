package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpVersion {

    HTTP1_1("HTTP/1.1"),
    HTTP2("HTTP/2"),
    ;

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion from(String requestHttpVersion) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.value.equals(requestHttpVersion))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 버전입니다."));
    }

    public String getValue() {
        return value;
    }
}
