package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpVersion {

    HTTP11("HTTP/1.1"),
    ;

    private final String httpVersion;

    HttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public static HttpVersion from(String version) {
        return Arrays.stream(values())
                .filter(value -> version.equalsIgnoreCase(value.httpVersion))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 HTTP VERSION 요청: " + version));
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
