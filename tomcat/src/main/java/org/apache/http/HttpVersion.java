package org.apache.http;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0"),
    ;
    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion getHttpVersion(String value) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 http 프로토콜 버전 : " + value + "입니다."));
    }

    public String getValue() {
        return value;
    }
}
