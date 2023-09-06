package org.apache.coyote.common;

import java.util.Objects;

public enum HttpProtocol {
    HTTP11("HTTP/1.1"),
    ;

    private final String value;

    HttpProtocol(String value) {
        this.value = value;
    }

    public static HttpProtocol from(String protocol) {
        if (Objects.equals(HTTP11.value, protocol)) {
            return HTTP11;
        }
        throw new IllegalArgumentException("지원되지 않는 HTTP 프로토콜 입니다.");
    }

    public String getValue() {
        return value;
    }
}
