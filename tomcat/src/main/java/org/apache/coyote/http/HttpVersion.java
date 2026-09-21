package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion from(final String source) {
        return Arrays.stream(values())
                .filter(version -> version.value.equals(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 버전입니다: " + source));
    }

    public String getValue() {
        return value;
    }
}
